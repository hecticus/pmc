package backend.job;

import akka.actor.Cancellable;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.basic.Config;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.*;
import play.libs.F.Promise;
import utils.Utils;

/**
 * Created by plesse on 7/10/14.
 */
public class EventManager extends HecticusThread {

    public EventManager(String name, AtomicBoolean run, Cancellable cancellable) {
        super("EventManager-"+name, run, cancellable);
    }

    public EventManager(String name, AtomicBoolean run) {
        super("EventManager-"+name, run);
    }

    public EventManager(AtomicBoolean run) {
        super("EventManager",run);
    }

    @Override
    public void process() {
        try{
            String eventString = RabbitMQ.getInstance().getNextEventLyra();
            if(eventString != null){
                ObjectNode event = (ObjectNode)Json.parse(eventString);
                String msg = event.get("msg").asText();
                long insertionTime = event.get("insertionTime").asLong();
//                Utils.printToLog(EventManager.class, "", "Procesando el evento "+msg, false, null, "support-level-1", Config.LOGGER_INFO);
                boolean valid = validateEvent(event);
                if(valid){
                    splitAndSendProcessRequest(event);
                } else {
                    Utils.printToLog(EventManager.class, "Evento Invalido", "El evento "+event.toString() + " no sera enviado por ser invalido", true, null, "support-level-1", Config.LOGGER_INFO);
                }
//                Utils.printToLog(EventManager.class, "", "Msg " + msg + ". Se proceso el evento en " + (System.currentTimeMillis() - insertionTime), false, null, "support-level-1", Config.LOGGER_INFO);
            }
        } catch (Exception ex) {
            Utils.printToLog(EventManager.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    private void splitAndSendProcessRequest(ObjectNode event) {
        Iterator<JsonNode> clients = event.get("clients").elements();
        event.remove("clients");
        int packagesSize = Config.getInt("packages-size");
        int count = 0;
        long appID = event.get("app").asLong();
        long insertionTime = event.get("insertionTime").asLong();
        String msg = event.get("msg").asText();
        ArrayList<JsonNode> clientsPage = new ArrayList<>(packagesSize);
        while(isAlive() && clients.hasNext()) {
            JsonNode cl = clients.next();
            if(count < packagesSize){
                clientsPage.add(cl);
                count++;
            } else {
//                ObjectNode finalEvent = generateEvent(appID, msg, insertionTime, clientsPage);
                ObjectNode finalEvent = generateEvent(event, clientsPage);
                sendProcessRequest(finalEvent);
                clientsPage.clear();
                count = 0;
                clientsPage.add(cl);
                count++;
            }
        }
        if(!clientsPage.isEmpty()){
//            ObjectNode finalEvent = generateEvent(appID, msg, insertionTime, clientsPage);
            ObjectNode finalEvent = generateEvent(event, clientsPage);
            sendProcessRequest(finalEvent);
            clientsPage.clear();
        }
    }

    private ObjectNode generateEvent(long appID, String msg, long insertionTime, ArrayList<JsonNode> clients) {
        ObjectNode finalEvent = Json.newObject();
        finalEvent.put("app", appID);
        finalEvent.put("msg", msg);
        finalEvent.put("insertionTime", insertionTime);
        finalEvent.put("emTime", System.currentTimeMillis());
        finalEvent.put("clients", Json.toJson(clients));
        finalEvent.put("emTime", System.currentTimeMillis());
        finalEvent.put("clients", Json.toJson(clients));
        return finalEvent;
    }

    private ObjectNode generateEvent(ObjectNode event, ArrayList<JsonNode> clients){
        ObjectNode finalEvent = event.deepCopy();
        finalEvent.put("emTime", System.currentTimeMillis());
        finalEvent.put("clients", Json.toJson(clients));
        return finalEvent;
    }

    private void sendProcessRequest(ObjectNode event) {
        try{
            Promise<WSResponse> result = WS.url("http://" + Config.getDaemonHost() + "/events/v1/process").post(event);
            ObjectNode response = (ObjectNode)result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
//            Utils.printToLog(EventManager.class, null,response.toString(), false, null, "support-level-1", Config.LOGGER_INFO);
            setAlive();
        }catch (Exception ex){
            Utils.printToLog(EventManager.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    private boolean validateEvent(ObjectNode event) {
        if(!event.has("app") || !event.has("msg") || !event.has("clients")){
            return false;
        }
        long appID = event.get("app").asLong();
        String msg = event.get("msg").asText();
        Application app = Application.finder.byId(appID);
        if(app == null){
            return false;
        }
        if(app.getActive() == 0){
            return false;
        }
        if(msg.isEmpty()){
            return false;
        }
        try{
            msg = URLDecoder.decode(msg, "UTF-8");
            event.put("msg", msg.length() > 100?msg.substring(0, 99):msg);
        }catch (Exception ex){
            return false;
        }
        return true;
    }
}
