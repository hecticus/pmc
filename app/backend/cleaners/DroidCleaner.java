package backend.cleaners;

import backend.Constants;
import backend.job.HecticusThread;
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

/**
 * Created by plessmann on 23/07/15.
 */
public class DroidCleaner extends Cleaner{

    @Override
    public void clean(Application app, ObjectNode event, HecticusThread invoker) {
        if(app.getCleanDeviceUrl() != null && !app.getCleanDeviceUrl().isEmpty()){
            Iterator<JsonNode> originalIds = event.get(Constants.ORIGINAL_IDS).elements();
            ArrayList<String> oids = new ArrayList<>();
            while(invoker.isAlive() && originalIds.hasNext()){
                oids.add(originalIds.next().asText());
            }
            Iterator<JsonNode> results = event.get(Constants.RESULTS).elements();
            int index = 0;
            ObjectNode operations = Json.newObject();
            ArrayList<ObjectNode> toClean = new ArrayList<>();
            while(invoker.isAlive() && results.hasNext()){
                boolean insert = false;
                ObjectNode result = (ObjectNode)results.next();
                ObjectNode operation = Json.newObject();
                if(result.has(Constants.ERROR)){
                    String error = result.get(Constants.ERROR).asText();
                    if(error.equalsIgnoreCase(Constants.NOT_REGISTERED)){
                        operation.put(Constants.OPERATION, Constants.DELETE);
                        insert = true;
                    }
                } else if(result.has(Constants.REGISTRATION_ID)){
                    operation.put(Constants.OPERATION, Constants.UPDATE);
                    operation.put(Constants.NEW_ID, result.get(Constants.REGISTRATION_ID).asText());
                    insert = true;
                }
                if(insert){
                    operation.put(Constants.PUSH_TYPE, getDevice().getName());
                    operation.put(Constants.ACTUAL_ID, oids.get(index));
                    toClean.add(operation);
                }
                index++;
            }
            operations.put(Constants.OPERATIONS, Json.toJson(toClean));
            try {
                F.Promise<WSResponse> resultWS = WS.url(app.getCleanDeviceUrl()).post(operations);
                ObjectNode response = (ObjectNode)resultWS.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
            } catch (Exception ex) {
                Utils.printToLog(DroidCleaner.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
            }
        }
    }
}
