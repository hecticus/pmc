package backend.job;

import akka.actor.Cancellable;
import backend.apns.JavApns;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hecticus.rackspacemailgun.MailGun;
import javapns.Push;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import javapns.notification.ResponsePacket;
import models.apps.Application;
import models.basic.Config;
import play.Play;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 7/15/14.
 *
 * Clase para hacer push de un evento, estos eventos tiene pocos clientes.
 */
public class HecticusPusher extends HecticusThread {

    /**
     * Evento a enviar por push
     */
    private ObjectNode event;
    public HecticusPusher(String name, AtomicBoolean run, Cancellable cancellable) {
        super("HecticusPusher-"+name, run, cancellable);
    }

    public HecticusPusher(AtomicBoolean run, Cancellable cancellable) {
        super("HecticusPusher", run, cancellable);
    }

    public HecticusPusher(String name, AtomicBoolean run) {
        super("HecticusPusher-"+name, run);
    }

    public HecticusPusher(AtomicBoolean run) {
        super("HecticusPusher",run);
    }

    public HecticusPusher(String name, AtomicBoolean run, ObjectNode event) {
        super("HecticusPusher-"+name, run);
        this.event = event;
    }

    /**
     * Metodo para procesar un evento y hacerle push.
     *
     * <p>
     *
     * Campos del evento:<br>
     * - app: id de la aplicacion
     * - insertionTime: epoch en el cual se inserto el evento en la cola EVENTS
     * - emTime: epoch en el cual el EventManager proceso el evento
     * - prodTime: epoch en el cual el HecticusProducer proceso el evento
     * - pmTime: epoch en el cual el PushManager proceso el evento
     * - type: tipo de device al que se enviara este evento (droid, ios, web, sms)
     * - regIDs: lista de los registrationIDs de Android o IOS, de correos o de msisdn
     * - msg: mensaje a enviar
     *
     * cuando un evento de Android o IOS se recibe y tiene canonicos o errores respectivamente,
     * se inserta un evento en la cola PUSH_RESULT para que sean tratados
     *
     * <p>
     *
     */
    @Override
    public void process() {
        try{
            long appID = event.get("app").asLong();
            long insertionTime = event.get("insertionTime").asLong();
            String type = event.get("type").asText();
            Application app = Application.finder.byId(appID);
            if(type.equalsIgnoreCase("droid")){
                sendDroidPushRequest(app);
            } else if(type.equalsIgnoreCase("ios")){
                int pooled = 1;
                try {pooled = Config.getInt("apns-pooled");} catch (Exception e){}
                if(pooled == 1){
                    sendIOSPushRequestPool(app);
                } else {
                    sendIOSPushRequest(app);
                }
            } else if(type.equalsIgnoreCase("web")){
                sendWEBPushRequest(app);
            } else if(type.equalsIgnoreCase("sms")){
                sendSMSPushRequest(app);
            } else {
                Utils.printToLog(HecticusPusher.class, "Tipo de push desconocido", "El tipo " + type + " no se reconoce. Evento: " + event.toString(), true, null, "support-level-1", Config.LOGGER_ERROR);
            }
            Utils.printToLog(HecticusPusher.class, "", (app.getDebug() == 1?"DEBUG ":"") + "Tipo " + type + ". Se proceso el evento: " + event.toString() + " en " + (System.currentTimeMillis() - insertionTime) + (event.has("generationTime")?" generado hace " + (System.currentTimeMillis() - event.get("generationTime").asLong()):""), false, null, "support-level-1", Config.LOGGER_INFO);
        }catch (Exception e){
            Utils.printToLog(HecticusPusher.class, "Error en el HecticusPusher", "El ocurrio un error en el HecticusPusher procesando el evento: " + event.toString(), true, e, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    /**
     * Metodo para hacer push de un evento por SMS (No esta implementado)
     *
     *
     * @param app   aplicacion asociada al evento de push
     */
    private void sendSMSPushRequest(Application app) {
        throw new UnsupportedOperationException("El metodo SMS no ha sido implementado");
    }

    /**
     * Metodo para hacer push de un evento por mailgun
     *
     * @param app   aplicacion asociada al evento de push
     */
    private void sendWEBPushRequest(Application app) {
        String regIDs = event.get("regIDs").asText();
        String msg = event.get("msg").asText();
        ObjectNode mail = (ObjectNode) event.get("mail");
        String title = app.getTitle();
        if (app.getDebug() == 0) {
            try {
                if(mail.has("title")){
                    title = mail.get("title").asText();
                }
                if(mail.has("message")){
                    msg = mail.get("message").asText();
                }
                Map sendSimpleMessage = null;
                if(mail.has("html") && mail.get("html").asBoolean()){
                    sendSimpleMessage = MailGun.sendHtmlMessage(app.getMailgunFrom(), app.getMailgunTo(), null, regIDs, title, msg, app.getMailgunApikey(), app.getMailgunApiurl());
                } else {
                    sendSimpleMessage = MailGun.sendSimpleMessage(app.getMailgunFrom(), app.getMailgunTo(), null, regIDs, title, msg, app.getMailgunApikey(), app.getMailgunApiurl());
                }
                if (sendSimpleMessage != null && (Integer) sendSimpleMessage.get("status") != 200) {
                    String emsg = "error en MailGun en el HecticusPusher, MailGun respondio " + sendSimpleMessage.toString();
                    Utils.printToLog(this, "Error en el HecticusPusher", emsg, true, null, "support-level-1", Config.LOGGER_ERROR);
                }
            } catch (Throwable t) {
                String emsg = "Proceso continua. Error en el MailGun, puede ser falta de librerias de jersey o de oauth";
                Utils.printToLog(this, "Error en el HecticusPusher", emsg, true, t, "support-level-1", Config.LOGGER_ERROR);
            }
        }
    }

    /**
     * Metodo para hacer push de un evento a IOS
     *
     * @param app   aplicacion asociada al evento de push
     */
    private void sendIOSPushRequest(Application app) {
        String regIDs = event.get("regIDs").asText();
        String msg = event.get("msg").asText();
        String[] registrationIds = regIDs.split(",");
        if(app.getDebug() == 0){
            try {
                PushedNotifications result = null;
//                File cert  = new File(Play.application().path().getAbsolutePath() + "/" + (app.getIosSandbox() == 0 ? app.getIosPushApnsCertProduction() : app.getIosPushApnsCertSandbox()));
                File cert  = new File((app.getIosSandbox() == 0 ? app.getIosPushApnsCertProduction() : app.getIosPushApnsCertSandbox()));
                if(app.getSound() != null && !app.getSound().isEmpty()){
                    result = Push.combined(msg, 0, app.getSound(), cert, app.getIosPushApnsPassphrase(), app.getIosSandbox() == 0, registrationIds);
                } else {
                    result = Push.alert(msg, cert, app.getIosPushApnsPassphrase(), app.getIosSandbox()==0, registrationIds);
                }
                PushNotificationPayload payload = PushNotificationPayload.alert(msg);
                if(app.getSound() != null && !app.getSound().isEmpty()){
                    payload.addSound(app.getSound());
                }
                JavApns.getInstance().enqueue(app, payload, registrationIds);
                ArrayList<String> failedIds = new ArrayList<>();
                if(result != null){
                    for (PushedNotification notification : result) {
                        if(!notification.isSuccessful()) {
                            String invalidToken = notification.getDevice().getToken();
                            failedIds.add(invalidToken);
                        }
                    }
                }
                if(!failedIds.isEmpty()){
                    ObjectNode response = Json.newObject();
                    response.put("original_ids", Json.toJson(registrationIds));
                    response.put("type", "IOS");
                    response.put("failed_ids", Json.toJson(failedIds));
                    long emTime = event.get("emTime").asLong();
                    long prodTime = event.get("prodTime").asLong();
                    long pmTime = event.get("pmTime").asLong();
                    long insertionTime = event.get("insertionTime").asLong();
                    response.put("emTime", emTime);
                    response.put("prodTime", prodTime);
                    response.put("pmTime", pmTime);
                    if(event.has("generationTime")) {
                        response.put("generationTime", event.get("generationTime").asLong());
                    }
                    response.put("insertionTime", insertionTime);
                    response.put("app", app.getIdApp());
                    try {
                        RabbitMQ.getInstance().insertPushResultLyra(response.toString());
                    } catch (Exception e) {
                        String emsg = "Proceso continua. Error insertando resultado de push en rabbit, response = " + response.toString();
                        Utils.printToLog(this, "Error en el HecticusPusher", emsg, true, e, "support-level-1", Config.LOGGER_ERROR);
                    }
                }
            } catch (Exception e) {
                Utils.printToLog(HecticusPusher.class, "Error en el HecticusPusher", "El ocurrio un error en el HecticusPusher procesando el evento: " + event.toString(), false, e, "support-level-1", Config.LOGGER_ERROR);
            }
        }
    }

    /**
     * Metodo para hacer push de un evento a Android
     *
     * @param app   aplicacion asociada al evento de push
     */
    private void sendDroidPushRequest(Application app) {
        String regIDs = event.get("regIDs").asText();
        String msg = event.get("msg").asText();
        String androidPushUrl = Config.getString("android-push-url");
        String[] registrationIds = regIDs.split(",");
        ObjectNode gcm = (ObjectNode) event.get("gcm");
        gcm.put("registration_ids", Json.toJson(registrationIds));
        if(app.getDebug() == 0){
            Promise<WSResponse> result = WS.url(androidPushUrl).setContentType("application/json").setHeader("Authorization","key="+app.getGoogleApiKey()).post(gcm);
            WSResponse r = null;
            String resp = null;
            try{
                r = result.get(Config.getLong("external-ws-timeout-millis"), TimeUnit.MILLISECONDS);
                ObjectNode response = (ObjectNode) r.asJson();
                resp = response.toString();
//                ObjectNode response = (ObjectNode) result.get(Config.getLong("external-ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
                if(response.has("canonical_ids") || (response.has("failure") && response.get("failure").asInt() > 0)){
                    response.put("original_ids", Json.toJson(registrationIds));
                    response.put("type", "DROID");
                    long emTime = event.get("emTime").asLong();
                    long prodTime = event.get("prodTime").asLong();
                    long pmTime = event.get("pmTime").asLong();
                    long insertionTime = event.get("insertionTime").asLong();
                    response.put("emTime", emTime);
                    response.put("prodTime", prodTime);
                    response.put("pmTime", pmTime);
                    if(event.has("generationTime")) {
                        response.put("generationTime", event.get("generationTime").asLong());
                    }
                    response.put("insertionTime", insertionTime);
                    response.put("app", app.getIdApp());
                    try {
                        RabbitMQ.getInstance().insertPushResultLyra(response.toString());
                    } catch (Exception e) {
                        String emsg = "Proceso continua. Error insertando resultado de push en rabbit, response = " + response.toString();
                        Utils.printToLog(this, "Error en el HecticusPusher", emsg, true, e, "support-level-1", Config.LOGGER_ERROR);
                    }
                }
            } catch (Exception e){
                try{
                    resp = r.asXml().toString();
                } catch(Exception e1){
                    resp = "la respuesta no es casteable a Json ni a XML";
                }
                Utils.printToLog(HecticusPusher.class, null, "Error en la respuesta de Google, resp: " + resp, false, e, "support-level-1", Config.LOGGER_ERROR);
            }
        }
    }

    /**
     * Metodo para hacer push de un evento a IOS usando el pool de conexiones
     *
     * @param app   aplicacion asociada al evento de push
     */
    private void sendIOSPushRequestPool(Application app) {
        String regIDs = event.get("regIDs").asText();
        String msg = event.get("msg").asText();
        String[] registrationIds = regIDs.split(",");
        if(app.getDebug() == 0){
            try {
                ObjectNode apns = (ObjectNode) event.get("apns");
                PushNotificationPayload payload = PushNotificationPayload.fromJSON(apns.toString());
                JavApns.getInstance().enqueue(app, payload, registrationIds);
            } catch (Exception e) {
                Utils.printToLog(HecticusPusher.class, "Error en el HecticusPusher", "El ocurrio un error en el HecticusPusher procesando el evento: " + event.toString(), false, e, "support-level-1", Config.LOGGER_ERROR);
            }
        }
    }

}
