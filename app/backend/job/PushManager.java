package backend.job;

import akka.actor.Cancellable;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.basic.Config;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase para procesar los eventos de la cola PUSH
 *
 * Created by plesse on 7/10/14.
 */
public class PushManager extends HecticusThread {

    public PushManager(String name, AtomicBoolean run, Cancellable cancellable) {
        super("PushManager"+name, run, cancellable);
    }

    public PushManager(String name, AtomicBoolean run) {
        super("PushManager"+name, run);
    }

    public PushManager(AtomicBoolean run) {
        super("PushManager", run);
    }

    /**
     * Metodo que toma un evento de la cola PUSH y lo envia a un HecticusPusher
     */
    @Override
    public void process() {
        try{
            String eventString = RabbitMQ.getInstance().getNextPushLyra();
            if(eventString != null){
                ObjectNode event = (ObjectNode) Json.parse(eventString);
                event.put("pmTime", System.currentTimeMillis());
                sendPushRequest(event);
            }
        } catch (Exception ex) {
            Utils.printToLog(PushManager.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    /**
     * Metodo que envia un evento para que un HecticusPusher lo envie a los devices
     *
     * @param event     evento a ser pusheado
     */
    private void sendPushRequest(ObjectNode event) {
        try{
            F.Promise<WSResponse> result = WS.url("http://" + Config.getDaemonHost() + "/events/v1/push").post(event);
            ObjectNode response = (ObjectNode)result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
        }catch (Exception ex){
            Utils.printToLog(PushManager.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }
}
