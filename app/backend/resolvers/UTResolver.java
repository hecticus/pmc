package backend.resolvers;

import backend.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Config;
import models.apps.Application;
import play.libs.Json;

import java.net.URLDecoder;
import java.util.Iterator;

/**
 * Created by plessmann on 21/10/15.
 */
public class UTResolver extends Resolver {



    public UTResolver() {
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
            JsonNode ep = event.get(Constants.EXTRA_PARAMS);
            if(ep.isTextual()){
                ep = Json.parse(ep.asText());
            }
            Iterator<String> iterator = ep.fieldNames();
            while (iterator.hasNext()){
                String next = iterator.next();
                message.put(next, ep.get(next));
            }
        }

        message.put(Constants.TITLE, app.getTitle());
        message.put(Constants.TEXT, msg);

        int length = message.toString().getBytes().length;
        while(length >= androidSize){
            msg = msg.substring(0, msg.length() - 1);
            message.put(Constants.CONTENT_TEXT, msg);
            message.put(Constants.TEXT, msg);
            length = message.toString().getBytes().length;
        }
        if(msg.isEmpty()){
            msg = event.get(Constants.MSG).asText();
            msg = msg.substring(0, 20);
            message.put(Constants.TEXT, msg);
        }

        ObjectNode fields = Json.newObject();
        fields.put(Constants.DATA, message);
        fields.put(Constants.COLLAPSE_KEY, app.getName());
        return fields;
    }
}
