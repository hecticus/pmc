package backend.resolvers;

import backend.Constants;
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
        String msg = event.get(Constants.MSG).asText();
        int androidSize = Config.getInt("android-payload-max-size");
        try{
            msg = URLDecoder.decode(msg, Constants.ENCODING_UTF_8);
        } catch (Exception ex){
            return null;
        }
        msg = msg.length() > Constants.PUSH_MAX_LENGTH ?msg.substring(0, Constants.PUSH_MAX_LENGTH -1):msg;
        ObjectNode message = Json.newObject();
        message.put(Constants.MESSAGE, msg);
        message.put(Constants.TITLE, app.getTitle());
        if(app.getSound() != null && !app.getSound().isEmpty()){
            message.put(Constants.SOUND, app.getSound());
        }
        ObjectNode extraParams = null;
        int extraParamsInt = 0;
        if(event.has(Constants.EXTRA_PARAMS)){
            Object ep = event.get(Constants.EXTRA_PARAMS);
            if(ep instanceof ObjectNode) {
                extraParams = (ObjectNode) event.get(Constants.EXTRA_PARAMS);
            } else if (ep instanceof TextNode){
                extraParams = (ObjectNode)Json.parse(((TextNode) ep).asText());
            } else{
                extraParamsInt = event.get(Constants.EXTRA_PARAMS).asInt();
            }
        } else {
            extraParams = event.deepCopy();
            extraParams.remove(Constants.REG_IDS);
            extraParams.remove(Constants.EM_TIME);
            extraParams.remove(Constants.PROD_TIME);
            extraParams.remove(Constants.PM_TIME);
            extraParams.remove(Constants.MSG);
            extraParams.remove(Constants.CLIENTS);
            extraParams.remove(Constants.GENERATION_TIME);
            extraParams.remove(Constants.INSERTION_TIME);
            extraParams.remove(Constants.APP);
        }

        if(extraParams != null) {
            message.put(Constants.EXTRA_PARAMS, extraParams);
        } else {
            message.put(Constants.EXTRA_PARAMS, extraParamsInt);
        }


        int length = message.toString().getBytes().length;
        while(length >= androidSize){
            msg = msg.substring(0, msg.length() - 1);
            message.put(Constants.MESSAGE, msg);
            length = message.toString().getBytes().length;
        }
        if(msg.isEmpty()){
            msg = event.get(Constants.MSG).asText();
            msg = msg.substring(0, 20);
            message.put(Constants.MESSAGE, msg);
        }

        ObjectNode fields = Json.newObject();
        fields.put(Constants.DATA, message);
        fields.put(Constants.COLLAPSE_KEY, app.getName());
        return fields;
    }
}
