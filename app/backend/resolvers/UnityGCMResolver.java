package backend.resolvers;

import backend.Constants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.Config;
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
        String msg = event.get(Constants.MSG).asText();
        int androidSize = Config.getInt("android-payload-max-size");
        try{
            msg = URLDecoder.decode(msg, Constants.ENCODING_UTF_8);
        } catch (Exception ex){
            return null;
        }
        msg = msg.length() > Constants.PUSH_MAX_LENGTH?msg.substring(0, Constants.PUSH_MAX_LENGTH-1):msg;
        ObjectNode message = Json.newObject();
        if(app.getSound() != null && !app.getSound().isEmpty()){
            message.put(Constants.SOUND, app.getSound());
        }
        if(event.has(Constants.EXTRA_PARAMS)){
            message.put(Constants.EXTRA_PARAMS, event.get(Constants.EXTRA_PARAMS));
        }

        message.put(Constants.TICKER, app.getTitle());
        message.put(Constants.CONTENT_TITLE,app.getTitle());
        message.put(Constants.CONTENT_TEXT,msg);

        int length = message.toString().getBytes().length;
        while(length >= androidSize){
            msg = msg.substring(0, msg.length() - 1);
            message.put(Constants.CONTENT_TEXT, msg);
            length = message.toString().getBytes().length;
        }
        if(msg.isEmpty()){
            msg = event.get(Constants.MSG).asText();
            msg = msg.substring(0, 20);
            message.put(Constants.CONTENT_TEXT, msg);
        }

        ObjectNode fields = Json.newObject();
        fields.put(Constants.DATA, message);
        fields.put(Constants.COLLAPSE_KEY, app.getName());
        return fields;
    }
}
