package backend.job;

import akka.actor.Cancellable;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import backend.rabbitmq.RabbitMQ;
import backend.resolvers.Resolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.basic.Config;
import models.basic.PushedEvent;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.*;
import play.libs.F.Promise;
import utils.Utils;

/**
 * Clase para tratar los eventos de la cola EVENTS
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

    /**
     * Metodo para obtener un evento de la cola EVENTS, validarlo, picarlo y enviarlo a un @{HecticusThread}.HecticusProducer
     */
    @Override
    public void process() {
        try{
            String eventString = RabbitMQ.getInstance().getNextEventLyra();
            if(eventString != null){
                ObjectNode event = (ObjectNode)Json.parse(eventString);
                boolean valid = validateEvent(event);
                if(valid){
                    splitAndSendProcessRequest(event);
                } else {
                    Utils.printToLog(EventManager.class, "Evento Invalido", "El evento " + event.toString() + " no sera enviado por ser invalido", true, null, "support-level-1", Config.LOGGER_INFO);
                }
            }
        } catch (Exception ex) {
            Utils.printToLog(EventManager.class, null, "Error procesando un evento", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    /**
     * Metodo para picar un evento en subeventos y enviarlo a un @{HecticusThread}.HecticusProducer
     *
     * @param event     evento a picar
     */
    private void splitAndSendProcessRequest(ObjectNode event) {
        Iterator<JsonNode> clients = event.get("clients").elements();
        event.remove("clients");
        int packagesSize = Config.getInt("packages-size");
        int count = 0;
        ArrayList<JsonNode> clientsPage = new ArrayList<>(packagesSize);
        while(isAlive() && clients.hasNext()) {
            JsonNode cl = clients.next();
            if(count < packagesSize){
                clientsPage.add(cl);
                count++;
            } else {
                ObjectNode finalEvent = generateEvent(event, clientsPage);
                sendProcessRequest(finalEvent);
                clientsPage.clear();
                count = 0;
                clientsPage.add(cl);
                count++;
            }
        }
        if(!clientsPage.isEmpty()){
            ObjectNode finalEvent = generateEvent(event, clientsPage);
            sendProcessRequest(finalEvent);
            clientsPage.clear();
        }
    }

    /**
     * Funcion para generar un sub evento
     *
     * @deprecated
     * @param appID             id de la aplicacion asociada al evento
     * @param msg               mensaje que se enviara
     * @param insertionTime     momento en que se inserto el evento en la cola EVENTS
     * @param clients           lista del subconjunto de clientes que se asociaran a este subevento
     * @return                  sub evento a enviar
     */
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

    /**
     * Funcion para generar un sub evento
     *
     * @param event         evento padre del que se copiara la data de push
     * @param clients       lista del subconjunto de clientes que se asociaran a este subevento
     * @return              sub evento a enviar
     */
    private ObjectNode generateEvent(ObjectNode event, ArrayList<JsonNode> clients){
        ObjectNode finalEvent = event.deepCopy();
        finalEvent.put("emTime", System.currentTimeMillis());
        finalEvent.put("clients", Json.toJson(clients));
        return finalEvent;
    }

    /**
     * Metodo para enviar el evento al WS que levantara el @{HecticusThread}.HecticusProducer que procesara el evento
     *
     * @param event     evento validado que se procesara
     */
    private void sendProcessRequest(ObjectNode event) {
        try{
            Promise<WSResponse> result = WS.url("http://" + Config.getDaemonHost() + "/events/v1/process").post(event);
            ObjectNode response = (ObjectNode)result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
            setAlive();
        }catch (Exception ex){
            Utils.printToLog(EventManager.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    /**
     * Metodo para validar que un evento y determinar si puede ser enviado.
     *
     * Un evento debe tener:
     * - app
     * - msg
     * - clients
     *
     * @param event     evento a validar
     * @return          true si puede ser enviado
     */
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
            Class jobClassName = Class.forName(app.getResolver().getClassName().trim());
            final Resolver androidResolver = (Resolver) jobClassName.newInstance();
            ObjectNode android = androidResolver.resolve(event, app);

            jobClassName = Class.forName("backend.resolvers.IOSResolver");
            final Resolver iosResolver = (Resolver) jobClassName.newInstance();
            ObjectNode aps = iosResolver.resolve(event, app);

            jobClassName = Class.forName("backend.resolvers.MailResolver");
            final Resolver mailResolver = (Resolver) jobClassName.newInstance();
            ObjectNode mail = mailResolver.resolve(event, app);

            if(android == null || aps == null || mail == null){
                return false;
            }
            try{
                msg = URLDecoder.decode(msg, "UTF-8");
            } catch (Exception ex){

            }
            event.put("gcm", android);
            event.put("apns", aps);
            event.put("mail", mail);
        }catch (Exception ex){
            return false;
        }

        PushedEvent pushedEvent = new PushedEvent(app, msg, System.currentTimeMillis(), event.get("clients").size());
        pushedEvent.save();
        return true;
    }
}
