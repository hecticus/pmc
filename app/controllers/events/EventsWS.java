package controllers.events;

import backend.apns.JavApns;
import backend.job.*;
//import backend.pushy.PushyManager;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hecticus.rackspacemailgun.MailGun;
//import com.relayrides.pushy.apns.*;
//import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
//import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
//import com.relayrides.pushy.apns.util.TokenUtil;
import controllers.HecticusController;
import controllers.basic.Secured;
import javapns.Push;
import javapns.notification.*;
import models.apps.Application;
import models.basic.Config;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.*;
import utils.Utils;

import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by plesse on 7/15/14.
 */
@Security.Authenticated(Secured.class)
public class EventsWS extends HecticusController {

    public static F.Promise<Result> launchProcess(Boolean prod) {
        final ObjectNode event = getJson();
        final Boolean producer = prod;
        F.Promise<ObjectNode> promiseOfObjectNode = F.Promise.promise(
                new F.Function0<ObjectNode>() {
                    public ObjectNode apply() {
                        return launchProc(event, producer);
                    }
                }
        );

        return promiseOfObjectNode.map(
                new F.Function<ObjectNode, Result>() {
                    public Result apply(ObjectNode i) {
                        return ok(i);
                    }
                }
        );
    }

    private static ObjectNode launchProc(ObjectNode event, boolean producerFlag){
        try{
            HecticusThread process = null;
            if(producerFlag){
                process = new HecticusProducer("WS", Utils.run, event);
            } else {
                process = new HecticusPusher("WS", Utils.run, event);
            }
            Thread th = new Thread(process);
            th.start();
            ObjectNode response = Json.newObject();
            response.put("error", 0);
            response.put("description", process.getName());
            return response;
        } catch (Exception ex) {
            ObjectNode response = Json.newObject();
            response.put("error", 1);
            response.put("description", ex.getMessage());
            return response;
        }
    }

    public static Result launchProducerOld() {
        try{
            ObjectNode event = getJson();
            HecticusThread producer = new HecticusProducer("WS",Utils.run, event);
            Thread th = new Thread(producer);
            th.start();
            ObjectNode response = Json.newObject();
            response.put("error", 0);
            response.put("description", producer.getName());
            return ok(response);
        } catch (Exception ex) {
            ObjectNode response = Json.newObject();
            response.put("error", 1);
            response.put("description", ex.getMessage());
            return Results.badRequest(response);
        }
    }

    public static F.Promise<Result> sendPush(Long idApp, Integer method) {
        final ObjectNode event = getJson();
        final Long idAppF = idApp;
        final Integer methodF = method;
        F.Promise<ObjectNode> promiseOfObjectNode = F.Promise.promise(
                new F.Function0<ObjectNode>() {
                    public ObjectNode apply() {
                        if(methodF == 0){
                            return sendDroidPushRequest(event, idAppF);
                        } else if(methodF == 1) {
                            return sendWEBPushRequest(event, idAppF);
                        } else if(methodF == 2) {
                            return sendIOSPushRequest(event, idAppF);
                        } else {
                            return sendIOSPushRequestPool(event, idAppF);
                        }
                    }
                }
        );

        return promiseOfObjectNode.map(
                new F.Function<ObjectNode, Result>() {
                    public Result apply(ObjectNode i) {
                        return ok(i);
                    }
                }
        );
    }

    private static ObjectNode sendDroidPushRequest(ObjectNode event, long idApp) {
        try{
            Application app = Application.finder.byId(idApp);
            String regIDs = event.get("regIDs").asText();
            String msg = event.get("msg").asText();
            String androidPushUrl = Config.getString("android-push-url");
            String[] registrationIds = regIDs.split(",");
            ObjectNode message = Json.newObject();
            message.put("message", msg);
            message.put("title", app.getTitle());
            if(app.getSound() != null && !app.getSound().isEmpty()){
                message.put("sound", app.getSound());
            }
            if(event.has("extra_params")){
                message.put("extra_params", event.get("extra_params"));
            } else {
                ObjectNode extraParams = event.deepCopy();
                extraParams.remove("regIDs");
                extraParams.remove("emTime");
                extraParams.remove("prodTime");
                extraParams.remove("pmTime");
                extraParams.remove("msg");
                extraParams.put("pushTime", System.currentTimeMillis());
                message.put("extra_params", extraParams);
            }
            ObjectNode fields = Json.newObject();
            fields.put("registration_ids", Json.toJson(registrationIds));
            fields.put("data", message);
            fields.put("collapse_key", app.getName());
            if(app.getDebug() == 0){
                F.Promise<WSResponse> result = WS.url(androidPushUrl).setContentType("application/json").setHeader("Authorization","key="+app.getGoogleApiKey()).post(fields);
                ObjectNode fResponse = Json.newObject();
                fResponse.put("response", Json.toJson((ObjectNode)result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson()));
                return fResponse;
            } else {
                ObjectNode fResponse = Json.newObject();
                fResponse.put("response", Json.toJson("call " + androidPushUrl + " with "+ fields.toString() + " and " + app.getGoogleApiKey()));
                return fResponse;
            }
        } catch (Exception ex) {
            ObjectNode response = Json.newObject();
            response.put("error", 1);
            response.put("description", ex.getMessage());
            return response;
        }

    }

    private static ObjectNode sendWEBPushRequest(ObjectNode event, long idApp) {
        Application app = Application.finder.byId(idApp);
        String regIDs = event.get("regIDs").asText();
        String msg = event.get("msg").asText();
        String title = app.getTitle();
        if (app.getDebug() == 0) {
            ObjectNode fResponse = Json.newObject();
            try {
                if(event.has("title")){
                    title = event.get("title").asText();
                }
                Map sendSimpleMessage = null;
                if(event.has("html") && event.get("html").asBoolean()){
                    sendSimpleMessage = MailGun.sendHtmlMessage(app.getMailgunFrom(), app.getMailgunTo(), null, regIDs, title, msg, app.getMailgunApikey(), app.getMailgunApiurl());
                } else {
                    sendSimpleMessage = MailGun.sendSimpleMessage(app.getMailgunFrom(), app.getMailgunTo(), null, regIDs, title, msg, app.getMailgunApikey(), app.getMailgunApiurl());
                }
                if (sendSimpleMessage != null && (Integer) sendSimpleMessage.get("status") != 200) {
                    String emsg = "error en MailGun en el HecticusPusher, MailGun respondio " + sendSimpleMessage.toString();
                    Utils.printToLog(EventsWS.class, "Error en el HecticusPusher", emsg, true, null, "support-level-1", Config.LOGGER_ERROR);
                }
                fResponse.put("response", Json.toJson(sendSimpleMessage));
            } catch (Throwable t) {
                String emsg = "Proceso continua. Error en el MailGun, puede ser falta de librerias de jersey o de oauth";
                Utils.printToLog(EventsWS.class, "Error en el HecticusPusher", emsg, true, t, "support-level-1", Config.LOGGER_ERROR);
                fResponse.put("error", 1);
                fResponse.put("description", t.getMessage());
            } finally {
                return fResponse;
            }
        } else {
            ObjectNode fResponse = Json.newObject();
            fResponse.put("response", Json.toJson("title = " + title+  " message = " + msg + " regIDs = " + regIDs));
            return fResponse;
        }
    }

    private static ObjectNode sendIOSPushRequest(ObjectNode event, long idApp) {
        Application app = Application.finder.byId(idApp);
        String regIDs = event.get("regIDs").asText();
        String msg = event.get("msg").asText();
        String[] registrationIds = regIDs.split(",");
        if(app.getDebug() == 0){
            ObjectNode fResponse = Json.newObject();
            try {
                fResponse.put("error", 0);
                fResponse.put("description", "");
                PushedNotifications result = null;
                if(app.getSound() != null && !app.getSound().isEmpty()){
                    result = Push.combined(msg, 0, app.getSound(), app.getIosSandbox()==0?app.getIosPushApnsCertProduction():app.getIosPushApnsCertSandbox(), app.getIosPushApnsPassphrase(), app.getIosSandbox()==0, registrationIds);
                } else {
                    result = Push.alert(msg, app.getIosSandbox()==0?app.getIosPushApnsCertProduction():app.getIosPushApnsCertSandbox(), app.getIosPushApnsPassphrase(), app.getIosSandbox()==0, registrationIds);
                }
                if(result != null){
                    for (PushedNotification notification : result) {
                        if(notification.isSuccessful()) {
                            System.out.println("Push notification sent successfully to: " + notification.getDevice().getToken());
                        } else {
                            String invalidToken = notification.getDevice().getToken();
                            System.out.println("Push notification sent FAILED to: " + invalidToken);
                            Exception theProblem = notification.getException();
                            theProblem.printStackTrace();
                            ResponsePacket theErrorResponse = notification.getResponse();
                            if (theErrorResponse != null) {
                                System.out.println(theErrorResponse.getMessage());
                            }
                        }
                    }
                }
                return fResponse;
            } catch (Exception e) {
                Utils.printToLog(EventsWS.class, "Error en el HecticusPusher", "El ocurrio un error en el HecticusPusher procesando el evento: " + event.toString(), false, e, "support-level-1", Config.LOGGER_ERROR);
                fResponse.put("error", 1);
                fResponse.put("description", e.getMessage());
            } finally {
                return fResponse;
            }
        } else {

            ObjectNode fResponse = Json.newObject();
            fResponse.put("response", "pushing " + msg + " to " + regIDs);
            return fResponse;
        }
    }

    private static ObjectNode sendIOSPushRequestPool(ObjectNode event, long idApp) {
        Application app = Application.finder.byId(idApp);
        String regIDs = event.get("regIDs").asText();
        String msg = event.get("msg").asText();
        String[] registrationIds = regIDs.split(",");
        if(app.getDebug() == 0){
            ObjectNode fResponse = Json.newObject();
            try {
                fResponse.put("error", 0);
                fResponse.put("description", "");
                PushedNotifications result = null;
                ObjectNode payloadToSend = Json.newObject();
                payloadToSend.put("alert", msg);
                if(event.has("extra_params")){
                    payloadToSend.put("extra_params", event.get("extra_params").asText());
                } else {
                    ObjectNode extraParams = event.deepCopy();
                    extraParams.remove("regIDs");
                    extraParams.remove("emTime");
                    extraParams.remove("prodTime");
                    extraParams.remove("pmTime");
                    extraParams.remove("msg");
                    extraParams.put("pushTime", System.currentTimeMillis());
                    payloadToSend.put("extra_params", extraParams.toString());
                }
                ObjectNode aps = Json.newObject();
                aps.put("aps", payloadToSend);
                PushNotificationPayload payload = PushNotificationPayload.fromJSON(aps.toString());
                if(app.getSound() != null && !app.getSound().isEmpty()){
                    payload.addSound(app.getSound());
                }
                fResponse.put("payload", payload.toString());
                fResponse.put("payload_bytes", payload.toString().getBytes().length);
                fResponse.put("app", app.toJson());
                System.out.println(payload.toString() + " size = " + payload.toString().getBytes().length);
                JavApns.getInstance().enqueue(app, payload, registrationIds);
                return fResponse;
            } catch (Exception e) {
                Utils.printToLog(EventsWS.class, "Error en el sendIOSPushRequestPool", "El ocurrio un error en el HecticusPusher procesando el evento: " + event.toString(), false, e, "support-level-1", Config.LOGGER_ERROR);
                fResponse.put("error", 1);
                fResponse.put("description", e.getMessage());
            } finally {
                return fResponse;
            }
        } else {

            ObjectNode fResponse = Json.newObject();
            fResponse.put("response", "pushing " + msg + " to " + regIDs);
            return fResponse;
        }
    }

    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 1024 * 1024)
    public static F.Promise<Result> insertEvent() {
        final ObjectNode event = getJson();
        F.Promise<ObjectNode> promiseOfObjectNode = F.Promise.promise(
                new F.Function0<ObjectNode>() {
                    public ObjectNode apply() {
                        return insertEv(event);
                    }
                }
        );

        return promiseOfObjectNode.map(
                new F.Function<ObjectNode, Result>() {
                    public Result apply(ObjectNode i) {
                        if(i.get("error").asInt() == 2){
                            return forbidden(i);
                        } else if(i.get("error").asInt() == 1){
                            return badRequest(i);
                        }
                        return ok(i);
                    }
                }
        );
    }

    private static ObjectNode insertEv(ObjectNode event) {
        ObjectNode fResponse = Json.newObject();
        try {
            int allowInsert = Config.getInt("allow-insert-events");
            if(allowInsert == 0){
                fResponse.put("error", 2);
                fResponse.put("description", "El servicio de insercion de eventos esta apagado");
                return fResponse;
            }
            fResponse.put("error", 0);
            fResponse.put("description", "");
            event.put("insertionTime", System.currentTimeMillis());
            fResponse.put("response", event);
            RabbitMQ.getInstance().insertEventLyra(event.toString());
            return fResponse;
        } catch (Exception e) {
            Utils.printToLog(EventsWS.class, "Error en el EventsWS", "El ocurrio un error en el EventsWS insertando el evento: " + event.toString(), false, e, "support-level-1", Config.LOGGER_ERROR);
            fResponse.put("error", 1);
            fResponse.put("description", e.getMessage());
        } finally {
            return fResponse;
        }

    }



}


