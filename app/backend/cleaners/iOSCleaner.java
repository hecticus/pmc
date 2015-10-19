package backend.cleaners;

import backend.Constants;
import backend.HecticusThread;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.Config;
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
public class iOSCleaner extends Cleaner {
    @Override
    public void clean(Application app, ObjectNode event, HecticusThread invoker) {
        if(app.getCleanDeviceUrl() != null && !app.getCleanDeviceUrl().isEmpty()){
            Iterator<JsonNode> failedIds = event.get(Constants.FAILED_IDS).elements();
            ObjectNode operations = Json.newObject();
            ArrayList<ObjectNode> toClean = new ArrayList<>();
            while(invoker.isAlive() && failedIds.hasNext()){
                ObjectNode result = (ObjectNode)failedIds.next();
                ObjectNode operation = Json.newObject();
                operation.put(Constants.OPERATION, Constants.DELETE);
                operation.put(Constants.ACTUAL_ID, result.asText());
                operation.put(Constants.PUSH_TYPE, getDevice().getName());
                toClean.add(operation);
            }
            operations.put(Constants.OPERATIONS, Json.toJson(toClean));
            try {
                F.Promise<WSResponse> resultWS = WS.url(app.getCleanDeviceUrl()).post(operations);
                ObjectNode response = (ObjectNode)resultWS.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
            } catch (Exception ex) {
                Utils.printToLog(iOSCleaner.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
            }
        }
    }
}
