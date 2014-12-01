package backend.resolvers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.basic.Config;
import play.libs.Json;

import java.net.URLDecoder;

/**
 * Created by plesse on 12/1/14.
 */
public class UnityGCMResolver extends Resolver {

    public UnityGCMResolver() {
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
        if(app.getSound() != null && !app.getSound().isEmpty()){
            message.put("sound", app.getSound());
        }
        if(event.has("extra_params")){
            message.put("extra_params", event.get("extra_params"));
        }

        message.put("ticker", app.getTitle());
        message.put("content_title",app.getTitle());
        message.put("content_text",msg);

        int length = message.toString().getBytes().length;
        while(length >= androidSize){
            msg = msg.substring(0, msg.length() - 1);
            message.put("content_text", msg);
            length = message.toString().getBytes().length;
        }
        if(msg.isEmpty()){
            msg = event.get("msg").asText();
            msg = msg.substring(0, 20);
            message.put("content_text", msg);
        }

        ObjectNode fields = Json.newObject();
        fields.put("data", message);
        fields.put("collapse_key", app.getName());
        return fields;
    }
}
