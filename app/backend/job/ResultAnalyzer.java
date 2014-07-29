package backend.job;

import akka.actor.Cancellable;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.basic.Config;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 7/25/14.
 */
public class ResultAnalyzer extends HecticusThread {
    public ResultAnalyzer(String name, AtomicBoolean run, Cancellable cancellable) {
        super("ResultAnalyzer-"+name, run, cancellable);
    }

    public ResultAnalyzer(String name, AtomicBoolean run) {
        super("ResultAnalyzer-"+name, run);
    }

    public ResultAnalyzer(AtomicBoolean run) {
        super("ResultAnalyzer", run);
    }

    @Override
    public void process() {
        try{
            String eventString = RabbitMQ.getInstance().getNextPushResultLyra();
            if(eventString != null){
                ObjectNode event = (ObjectNode) Json.parse(eventString);
                String type = event.get("type").asText();
                long appID = event.get("app").asLong();
                Application app = Application.finder.byId(appID);
                if(type.equalsIgnoreCase("droid")){
                    cleanDroidDevices(app, event);
                } else if(type.equalsIgnoreCase("ios")){
                    cleanIOSDevices(app, event);
                }
            }
        } catch (Exception ex) {
            Utils.printToLog(ResultAnalyzer.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }

    }

    private void cleanIOSDevices(Application app, ObjectNode event) {
        if(app.getCleanDeviceUrl() != null && !app.getCleanDeviceUrl().isEmpty()){
            Iterator<JsonNode> failedIds = event.get("failed_ids").elements();
            ObjectNode operations = Json.newObject();
            ArrayList<ObjectNode> toClean = new ArrayList<>();
            while(isAlive() && failedIds.hasNext()){
                ObjectNode result = (ObjectNode)failedIds.next();
                ObjectNode operation = Json.newObject();
                operation.put("operation", "DELETE");
                operation.put("actual_id", result.asText());
                operation.put("type", "ios");
                toClean.add(operation);
            }
            operations.put("operations", Json.toJson(toClean));
            try {
                F.Promise<WSResponse> resultWS = WS.url(app.getCleanDeviceUrl()).post(operations);
                ObjectNode response = (ObjectNode)resultWS.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
            } catch (Exception ex) {
                Utils.printToLog(ResultAnalyzer.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
            }
        }
    }

    private void cleanDroidDevices(Application app, ObjectNode event) {
        if(app.getCleanDeviceUrl() != null && !app.getCleanDeviceUrl().isEmpty()){
            Iterator<JsonNode> originalIds = event.get("original_ids").elements();
            ArrayList<String> oids = new ArrayList<>();
            while(isAlive() && originalIds.hasNext()){
                oids.add(originalIds.next().asText());
            }
            Iterator<JsonNode> results = event.get("results").elements();
            int index = 0;
            ObjectNode operations = Json.newObject();
            ArrayList<ObjectNode> toClean = new ArrayList<>();
            while(isAlive() && results.hasNext()){
                boolean insert = false;
                ObjectNode result = (ObjectNode)results.next();
                ObjectNode operation = Json.newObject();
                if(result.has("error")){
                    String error = result.get("error").asText();
                    if(error.equalsIgnoreCase("NotRegistered")){
                        operation.put("operation", "DELETE");
                        insert = true;
                    }
                } else if(result.has("registration_id")){
                    operation.put("operation", "UPDATE");
                    operation.put("new_id", result.get("registration_id").asText());
                    insert = true;
                }
                if(insert){
                    operation.put("type", "droid");
                    operation.put("actual_id", oids.get(index));
                    toClean.add(operation);
                }
                index++;
            }
            operations.put("operations", Json.toJson(toClean));
            try {
                F.Promise<WSResponse> resultWS = WS.url(app.getCleanDeviceUrl()).post(operations);
                ObjectNode response = (ObjectNode)resultWS.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
            } catch (Exception ex) {
                Utils.printToLog(ResultAnalyzer.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
            }
        }
    }

}
