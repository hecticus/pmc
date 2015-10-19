package controllers.events;

import backend.HecticusThread;
import backend.ServerInstance;
import backend.job.HecticusProducer;
import backend.job.HecticusPusher;
import backend.pushers.Pusher;
import backend.rabbitmq.RabbitMQ;
import backend.resolvers.Resolver;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.HecticusController;
import models.Config;
import models.apps.AppDevice;
import models.apps.Application;
import play.libs.F;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

//import backend.pushy.PushyManager;
//import com.relayrides.pushy.apns.*;
//import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
//import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
//import com.relayrides.pushy.apns.util.TokenUtil;


/**
 * Created by plesse on 7/15/14.
 */
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
            AtomicBoolean instanceRun = ServerInstance.getInstance().isInstanceRun();
            if(producerFlag){
                process = new HecticusProducer("WS", instanceRun, event);
            } else {
                process = new HecticusPusher("WS", instanceRun, event);
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
            AtomicBoolean instanceRun = ServerInstance.getInstance().isInstanceRun();
            HecticusThread producer = new HecticusProducer("WS", instanceRun, event);
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
        final Application app = Application.finder.byId(idApp);
        final Integer methodF = method;
        F.Promise<ObjectNode> promiseOfObjectNode = F.Promise.promise(
            new F.Function0<ObjectNode>() {
                public ObjectNode apply() {
                    try {
                        AppDevice device = null;
                        if(methodF == 0){
                            device = app.getDevice("droid");
                        } else if(methodF == 1) {
                            device = app.getDevice("web");
                        } else if(methodF == 2) {
                            device = app.getDevice("ios");
                        } else {
                            return buildBasicResponse(1, "Error: El metodo " + methodF + " no esta disponible para el app" + app.getName());
                        }

                        if (device != null) {
                            ObjectNode resolved;
                            Class resolverClassName = Class.forName(device.getResolver().getClassName().trim());
                            if(resolverClassName != null){
                                Resolver resolver = (Resolver) resolverClassName.newInstance();
                                if(resolver != null){
                                    resolved = resolver.resolve(event, app);
                                    if(resolved != null){
                                        event.put(device.getDev().getName(), resolved);
                                    } else {
                                        return buildBasicResponse(3, "Error: no se pudo resolver el mensaje");
                                    }
                                } else {
                                    return buildBasicResponse(2, "Error: no se consiguio el resolver");
                                }
                            } else {
                                return buildBasicResponse(1, "Error: no se consiguio el resolver");
                            }

                            Class pusherClassName = Class.forName(device.getPusher().getClassName().trim());
                            if (pusherClassName != null) {
                                Pusher pusher = (Pusher) pusherClassName.newInstance();
                                if (pusher != null) {
                                    pusher.setDevice(device.getDev());
                                    pusher.setParams(device.getPusher().getParsedParams());
                                    pusher.setInsertResult(false);
                                    return pusher.push(event, app);
                                } else {
                                    return buildBasicResponse(6, "Error: no se pudo cargar el pusher");
                                }
                            } else {
                                return buildBasicResponse(5, "Error: no existe el pusher");
                            }
                        } else {
                            return buildBasicResponse(4, "Error: no existe el device");
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        return buildBasicResponse(-1, "Error", e);
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

    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 1024 * 1024 * 1024)
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

    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 1024 * 1024 * 1024)
    public static F.Promise<Result> insertWebEvent() {
        final ObjectNode event = getJson();
        ArrayList<String> filter = new ArrayList<>();
        filter.add("web");
        event.put("devices_to_send", Json.toJson(filter));
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

    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 1024 * 1024)
    public static F.Promise<Result> insertAndroidEvent() {
        final ObjectNode event = getJson();
        ArrayList<String> filter = new ArrayList<>();
        filter.add("android");
        event.put("devices_to_send", Json.toJson(filter));
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

    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 1024 * 1024)
    public static F.Promise<Result> insertIosEvent() {
        final ObjectNode event = getJson();
        ArrayList<String> filter = new ArrayList<>();
        filter.add("ios");
        event.put("devices_to_send", Json.toJson(filter));
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


