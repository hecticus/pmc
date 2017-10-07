package backend.job;

import akka.actor.Cancellable;
import backend.Constants;
import backend.HecticusThread;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Config;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase para procesar los eventos de la cola PUSH
 *
 * Created by plesse on 7/10/14.
 */
public class PushManager extends HecticusThread {

    public PushManager() {
        this.setActTime(System.currentTimeMillis());
        this.setInitTime(System.currentTimeMillis());
        this.setPrevTime(System.currentTimeMillis());
        //set name
        this.setName("PushManager-" + System.currentTimeMillis());
    }

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
    public void process(Map args) {
        try{

            String eventString = RabbitMQ.getInstance().getNextPushLyra();
            if(eventString != null){
                ObjectNode event = (ObjectNode) Json.parse(eventString);
                event.put(Constants.PM_TIME, System.currentTimeMillis());
                sendPushRequest(event);
            }
        } catch (Exception ex) {
            Utils.printToLog(PushManager.class, null, "Error procesando un evento de la cola PUSH", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    /**
     * Metodo que envia un evento para que un HecticusPusher lo envie a los devices
     *
     * @param event     evento a ser pusheado
     */
    private void sendPushRequest(ObjectNode event) {
        try{
            F.Promise<WSResponse> result = WS.url(String.format(Constants.WS_PUSH_EVENT, Config.getPMCHost())).post(event);
            ObjectNode response = (ObjectNode)result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();

        }catch (Exception ex){
            Utils.printToLog(PushManager.class, null, "Error en el WS de distribucion de eventos", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }
}
