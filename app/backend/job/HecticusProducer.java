package backend.job;

import akka.actor.Cancellable;
import backend.Constants;
import backend.HecticusThread;
import backend.caches.Client;
import backend.caches.ClientsCache;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Config;
import models.apps.AppDevice;
import models.apps.Application;
import utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase que genera subeventos con los RegistrationIDs y los inserta en la cola PUSH
 * Created by plesse on 7/10/14.
 */
public class HecticusProducer extends HecticusThread {

    private ObjectNode event;
    private ArrayList<AppDevice> allowedDevices;

    public HecticusProducer() {
        this.setActTime(System.currentTimeMillis());
        this.setInitTime(System.currentTimeMillis());
        this.setPrevTime(System.currentTimeMillis());
        //set name
        this.setName("HecticusProducer-" + System.currentTimeMillis());
    }

    public HecticusProducer(String name, AtomicBoolean run, Cancellable cancellable) {
        super("HecticusProducer-"+name, run, cancellable);
    }

    public HecticusProducer(AtomicBoolean run, Cancellable cancellable) {
        super("HecticusProducer", run, cancellable);
    }

    public HecticusProducer(AtomicBoolean run) {
        super("HecticusProducer", run);
    }

    public HecticusProducer(String name, AtomicBoolean run) {
        super("HecticusProducer-"+name, run);
    }

    public HecticusProducer(String name, AtomicBoolean run, ObjectNode event) {
        super("HecticusProducer-"+name, run);
        this.event = event;
    }

    /**
     * Metodo que recibe un evento de clientes, lo pica en sub eventos de RegistrationIDs y lo inserta en PUSH
     */
    @Override
    public void process(Map args) {
        try{
            allowedDevices = new ArrayList<>();
            long appID = event.get(Constants.APP).asLong();
            String msg = event.get(Constants.MSG).asText();
            int pushSize = Config.getInt("push-size");
            Iterator<JsonNode> clients = event.get(Constants.CLIENTS).elements();
            event.remove(Constants.CLIENTS);
            ClientsCache cc = ClientsCache.getInstance();
            int count = 0;
            Application app = Application.finder.byId(appID);
            boolean delimited = event.has(Constants.DEVICES_TO_SEND);
            if(delimited){
                Iterator<JsonNode> devicesToSend = event.get(Constants.DEVICES_TO_SEND).elements();
                String next;
                AppDevice device;
                while(devicesToSend.hasNext()){
                    next = devicesToSend.next().asText();
                    device = app.getDevice(next);
                    if(device != null && device.getStatus()){
                        allowedDevices.add(device);
                    }
                }
                event.remove(Constants.DEVICES_TO_SEND);
            } else {
                for(AppDevice device : app.getAppDevices()){
                    if(device.getStatus()){
                        allowedDevices.add(device);
                    }
                }
            }
            while(clients.hasNext()){
                JsonNode clientID = clients.next();
                String key = String.format(Constants.CLIENT_CACHE_KEY_TEMPLATE, appID, clientID.asText());
                try {
                    Client client = cc.getClient(key);
                    if(client != null) {
                        if (count < pushSize) {
                            client.sortRegIDs(allowedDevices);
                            ++count;
                        } else {
                            processEvent();
                            client.sortRegIDs(allowedDevices);
                            count = 1;
                        }
                    }
                } catch (Exception ex) {
                    
                    //Log key if appID=1.
                    if (appID == 1) {
                        Utils.printToLog(HecticusProducer.class, "Hecticus producer - TVMAX", "DEBUG: Event: \"" + msg + "\" - ClientKey: " + key, true, ex, "support-level-1", Config.LOGGER_ERROR);
                    }
                    Utils.printToLog(HecticusProducer.class, null, "No se enviara el evento \"" + msg + "\" al cliente " + key, false, ex, "support-level-1", Config.LOGGER_ERROR);
                    
                }
            }
            processEvent();
        }catch (Exception e){
            Utils.printToLog(HecticusProducer.class, "Error en el HecticusProducer", "Ocurrio un error en el HecticusProducer procesando el evento: " + event.toString(), true, e, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    /**
     * Metodo para generar en insertar un evento de push en la cola PUSH
     *
     * @param type          tipo de dispositivo de los RegistrationIDs (droid, ops, web,etc)
     * @param event         evento original sin la lista de clientes
     * @param regIDs        String CSV con los RegistrationIDs del tipo type
     */
    private void generateEvent(String type, ObjectNode event, String regIDs){
        ObjectNode finalEvent = event.deepCopy();
        finalEvent.put(Constants.PUSH_TYPE, type);
        finalEvent.put(Constants.PROD_TIME, System.currentTimeMillis());
        finalEvent.put(Constants.REG_IDS, regIDs);
        try {
            RabbitMQ.getInstance().insertPushLyra(finalEvent.toString());
        } catch (Exception ex) {
            Utils.printToLog(HecticusProducer.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    private void processEvent(){
        for (AppDevice allowedDevice : allowedDevices){
            StringBuilder idsBuilder = allowedDevice.getIds();
            if(idsBuilder != null){
                String ids = idsBuilder.toString();
                if(ids != null && !ids.isEmpty()){
                    generateEvent(allowedDevice.getDev().getName(), event, ids);
                    allowedDevice.clearIds();
                }
            }
        }
    }


}
