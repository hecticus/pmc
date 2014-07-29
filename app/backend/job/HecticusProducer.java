package backend.job;

import akka.actor.Cancellable;
import backend.caches.Client;
import backend.caches.ClientsCache;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.AppDevice;
import models.apps.Application;
import models.basic.Config;
import play.libs.Json;
import utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 7/10/14.
 */
public class HecticusProducer extends HecticusThread{

    private ObjectNode event;
    private boolean pDroid = false;
    private boolean pIOS = false;
    private boolean pWEB = false;
    private boolean pMsisdn = false;
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

    @Override
    public void process() {
        try{
            long appID = event.get("app").asLong();
            String msg = event.get("msg").asText();
            long insertionTime = event.get("insertionTime").asLong();
            long emTime = event.get("emTime").asLong();
            int pushSize = Config.getInt("push-size");
            Iterator<JsonNode> clients = event.get("clients").elements();
            event.remove("clients");
            ClientsCache cc = ClientsCache.getInstance();
            StringBuilder droidIDs = new StringBuilder();
            StringBuilder iosIDs = new StringBuilder();
            StringBuilder webIDs = new StringBuilder();
            StringBuilder msisdnIDs = new StringBuilder();
            int count = 0;
            int total = 0;
            Application app = Application.finder.byId(appID);
            List<AppDevice> appDevices = app.getAppDevices();
            for(AppDevice ad : appDevices){
                if(ad.getDev().getName().equalsIgnoreCase("droid") && ad.getStatus() == 1) {
                    pDroid = true;
                } else if(ad.getDev().getName().equalsIgnoreCase("ios") && ad.getStatus() == 1) {
                    pIOS = true;
                } else if(ad.getDev().getName().equalsIgnoreCase("web") && ad.getStatus() == 1) {
                    pWEB = true;
                } else if(ad.getDev().getName().equalsIgnoreCase("msisdn") && ad.getStatus() == 1) {
                    pMsisdn = true;
                } else {
//                    Utils.printToLog(HecticusProducer.class, "Tipo de push desconocido", "El device  " + ad.getDev().getName() + " no se reconoce", false, null, "support-level-1", Config.LOGGER_ERROR);
                }
            }
            while(clients.hasNext()){
                total++;
                String key = appID + "-" + clients.next().asText();
                try {
                    Client client = cc.getClient(key);
                    if(count < pushSize){
                        sortClientRegIDs(client, droidIDs, iosIDs, webIDs, msisdnIDs);
                        count++;
                    } else {
                        if(!droidIDs.toString().isEmpty()){
                            generateEvent("DROID", event,  droidIDs.toString());
                            droidIDs.delete(0, droidIDs.length());
                        }

                        if(!iosIDs.toString().isEmpty()){
                            generateEvent("IOS", event,  iosIDs.toString());
                            iosIDs.delete(0, iosIDs.length());
                        }

                        if(!webIDs.toString().isEmpty()){
                            generateEvent("WEB", event,  webIDs.toString());
                            webIDs.delete(0, webIDs.length());
                        }

                        if(!msisdnIDs.toString().isEmpty()){
                            generateEvent("SMS", event,  msisdnIDs.toString());
                            msisdnIDs.delete(0, msisdnIDs.length());
                        }
                        count = 0;
                        sortClientRegIDs(client, droidIDs, iosIDs, webIDs, msisdnIDs);
                        count++;
                    }
                } catch (Exception ex) {
                    Utils.printToLog(HecticusProducer.class, null, "No se enviara el evento \"" + msg + "\" al cliente " + key, false, null, "support-level-1", Config.LOGGER_ERROR);
                }

            }
            if(!droidIDs.toString().isEmpty()){
                generateEvent("DROID", event,  droidIDs.toString());
                droidIDs.delete(0, droidIDs.length());
            }

            if(!iosIDs.toString().isEmpty()){
                generateEvent("IOS", event,  iosIDs.toString());
                iosIDs.delete(0, iosIDs.length());
            }

            if(!webIDs.toString().isEmpty()){
                generateEvent("WEB", event,  webIDs.toString());
                webIDs.delete(0, webIDs.length());
            }

            if(!msisdnIDs.toString().isEmpty()){
                generateEvent("SMS", event,  msisdnIDs.toString());
                msisdnIDs.delete(0, msisdnIDs.length());
            }
        }catch (Exception e){
            Utils.printToLog(HecticusProducer.class, "Error en el HecticusProducer", "Ocurrio un error en el HecticusProducer procesando el evento: " + event.toString(), true, e, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    private void sortClientRegIDs(Client client, StringBuilder droidIDs, StringBuilder iosIDs, StringBuilder webIDs, StringBuilder msisdnIDs) {
        if(client != null){
            ArrayList<String> droid = client.getDroid();
            if(pDroid && droid != null && !droid.isEmpty()){
                for(String id : droid){
                    droidIDs.append(id).append(",");
                }
            }
            ArrayList<String> ios = client.getIos();
            if(pIOS && ios != null && !ios.isEmpty()){
                for(String id : ios){
                    iosIDs.append(id).append(",");
                }
            }
            ArrayList<String> web = client.getWeb();
            if(pWEB && web != null && !web.isEmpty()){
                for(String id : web){
                    webIDs.append(id).append(",");
                }
            }
            String msisdn = client.getMsisdn();
            if(pMsisdn && msisdn != null && !msisdn.isEmpty()){
                msisdnIDs.append(msisdn).append(",");
            }
        }
    }

    private void generateEvent(String type, String msg, long app, long insertionTime, long emTime, String regIDs) {
        ObjectNode event = Json.newObject();
        event.put("msg", msg);
        event.put("app", app);
        event.put("type", type);
        event.put("insertionTime", insertionTime);
        event.put("emTime", emTime);
        event.put("prodTime", System.currentTimeMillis());
        event.put("regIDs", regIDs);
        try {
            RabbitMQ.getInstance().insertPushLyra(event.toString());
        } catch (Exception ex) {
            Utils.printToLog(HecticusProducer.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }

    private void generateEvent(String type, ObjectNode event, String regIDs){
        ObjectNode finalEvent = event.deepCopy();
        finalEvent.put("type", type);
        finalEvent.put("prodTime", System.currentTimeMillis());
        finalEvent.put("regIDs", regIDs);
        try {
            RabbitMQ.getInstance().insertPushLyra(finalEvent.toString());
        } catch (Exception ex) {
            Utils.printToLog(HecticusProducer.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }


}
