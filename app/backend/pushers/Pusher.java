package backend.pushers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.apps.Device;
import models.Config;
import play.libs.Json;

import java.util.Map;

/**
 * Created by plessmann on 21/07/15.
 */
public abstract class Pusher {

    private Map params;

    private boolean insertResult;

    private Device device;

    protected Pusher() {
    }

    protected Pusher(boolean insertResult) {
        this.insertResult = insertResult;
    }

    protected Pusher(Map params, boolean insertResult) {
        this.params = params;
        this.insertResult = insertResult;
    }

    public boolean canInsertResult() {
        return insertResult;
    }

    public void setInsertResult(boolean insertResult) {
        this.insertResult = insertResult;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public abstract ObjectNode push(ObjectNode event, Application app);

    public static ObjectNode buildErrorResponse(int code, String responseMsg, Throwable e) {
        ObjectNode responseNode = Json.newObject();
        responseNode.put(Config.ERROR_KEY, code);
        responseNode.put(Config.DESCRIPTION_KEY, responseMsg);
        responseNode.put(Config.EXCEPTION_KEY, e.getMessage());
        return responseNode;
    }

    public static ObjectNode buildBasicResponse(int code, String responseMsg, JsonNode obj) {
        ObjectNode responseNode = Json.newObject();
        responseNode.put(Config.ERROR_KEY, code);
        responseNode.put(Config.DESCRIPTION_KEY, responseMsg);
        responseNode.put(Config.RESPONSE_KEY,obj);
        return responseNode;
    }

    public static ObjectNode buildBasicResponse(int code, String responseMsg) {
        ObjectNode responseNode = Json.newObject();
        responseNode.put(Config.ERROR_KEY, code);
        responseNode.put(Config.DESCRIPTION_KEY, responseMsg);
        return responseNode;
    }

}
