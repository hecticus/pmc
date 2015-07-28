package backend.job;

import akka.actor.Cancellable;
import backend.Constants;
import backend.HecticusThread;
import backend.pushers.Pusher;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.AppDevice;
import models.apps.Application;
import models.Config;
import utils.Utils;

import java.util.Map;
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

    public HecticusPusher() {
        this.setActTime(System.currentTimeMillis());
        this.setInitTime(System.currentTimeMillis());
        this.setPrevTime(System.currentTimeMillis());
        //set name
        this.setName("HecticusPusher-" + System.currentTimeMillis());
    }

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
    public void process(Map args) {
        try{
            if(event != null) {
                long appID = event.get(Constants.APP).asLong();
                String type = event.get(Constants.PUSH_TYPE).asText();
                Application app = Application.finder.byId(appID);
                if(app != null) {
                    AppDevice device = app.getDevice(type);
                    if (device != null) {
                        Class pusherClassName = Class.forName(device.getPusher().getClassName().trim());
                        if (pusherClassName != null) {
                            Pusher pusher = (Pusher) pusherClassName.newInstance();
                            if (pusher != null) {
                                pusher.setParams(device.getPusher().getParsedParams());
                                pusher.setDevice(device.getDev());
                                pusher.setInsertResult(true);
                                pusher.push(event, app);
                            } else {
                                Utils.printToLog(HecticusPusher.class, "No hay pusher para el evento", "El tipo " + type + " no tiene pusher. Evento: " + event.toString(), true, null, "support-level-1", Config.LOGGER_ERROR);
                            }
                        } else {
                            Utils.printToLog(HecticusPusher.class, "No hay pusher para el evento", "El tipo " + type + " no tiene pusher. Evento: " + event.toString(), true, null, "support-level-1", Config.LOGGER_ERROR);
                        }
                    } else {
                        Utils.printToLog(HecticusPusher.class, "Tipo de push desconocido", "El app no dispone del " + type + " no se reconoce. Evento: " + event.toString(), false, null, "support-level-1", Config.LOGGER_ERROR);
                    }
                } else {
                    Utils.printToLog(HecticusPusher.class, "Tipo de push desconocido", "El app no no se reconoce. Evento: " + event.toString(), false, null, "support-level-1", Config.LOGGER_ERROR);
                }
            } else {
                Utils.printToLog(HecticusPusher.class, "Error en el HecticusPusher", "Llego un evento nulo", true, null, "support-level-1", Config.LOGGER_INFO);
            }
        }catch (Exception e){
            Utils.printToLog(HecticusPusher.class, "Error en el HecticusPusher", "El ocurrio un error en el HecticusPusher procesando el evento: " + event.toString(), true, e, "support-level-1", Config.LOGGER_ERROR);
        }
    }
}
