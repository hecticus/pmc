package backend.resolvers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import models.apps.Application;
import models.basic.Config;
import play.libs.Json;

import java.net.URLDecoder;

/**
 * Created by plesse on 12/1/14.
 */
public class HecticusResolver extends Resolver {

    public HecticusResolver() {
    }

    @Override
    public ObjectNode resolve(ObjectNode event, Application app) {
        String msg = event.get("msg").asText();
        int androidSize = Config.getInt("android-payload-max-size");
        try{
            msg = URLDecoder.decode(msg, "UTF-8");
        } catch (Exception ex){
            return null;
        }
        msg = msg.length() > 100?msg.substring(0, 99):msg;
        ObjectNode message = Json.newObject();
        message.put("message", msg);
        message.put("title", app.getTitle());
        if(app.getSound() != null && !app.getSound().isEmpty()){
            message.put("sound", app.getSound());
        }
        ObjectNode extraParams = null;
        int extraParamsInt = 0;
        if(event.has("extra_params")){
            Object ep = event.get("extra_params");
            if(ep instanceof ObjectNode) {
                extraParams = (ObjectNode) event.get("extra_params");
            } else if (ep instanceof TextNode){
                extraParams = (ObjectNode)Json.parse(((TextNode) ep).asText());
            } else{
                extraParamsInt = event.get("extra_params").asInt();
            }
        } else {
            extraParams = event.deepCopy();
            extraParams.remove("regIDs");
            extraParams.remove("emTime");
            extraParams.remove("prodTime");
            extraParams.remove("pmTime");
            extraParams.remove("msg");
            extraParams.remove("clients");
            extraParams.remove("generationTime");
            extraParams.remove("insertionTime");
            extraParams.remove("app");
        }

        if(extraParams != null) {
            message.put("extra_params", extraParams);
        } else {
            message.put("extra_params", extraParamsInt);
        }


        int length = message.toString().getBytes().length;
        while(length >= androidSize){
            msg = msg.substring(0, msg.length() - 1);
            message.put("message", msg);
            length = message.toString().getBytes().length;
        }
        if(msg.isEmpty()){
            msg = event.get("msg").asText();
            msg = msg.substring(0, 20);
            message.put("message", msg);
        }

        ObjectNode fields = Json.newObject();
        fields.put("data", message);
        fields.put("collapse_key", app.getName());
        return fields;
    }
}
