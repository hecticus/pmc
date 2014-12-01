package backend.resolvers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.*;
import models.basic.Config;
import play.libs.Json;

import java.net.URLDecoder;

/**
 * Created by plesse on 12/1/14.
 */
public class IOSResolver extends Resolver {

    @Override
    public ObjectNode resolve(ObjectNode event, Application app) {
        String msg = event.get("msg").asText();
        int iosSize = Config.getInt("ios-payload-max-size");
        try{
            msg = URLDecoder.decode(msg, "UTF-8");
        } catch (Exception ex){
            return null;
        }
        msg = msg.length() > 100?msg.substring(0, 99):msg;

        ObjectNode payloadToSend = Json.newObject();
        payloadToSend.put("alert", msg);

        ObjectNode extraParams = null;
        int extraParamsInt = 0;

        if(event.has("extra_params")){
            Object ep = event.get("extra_params");
            if(ep instanceof ObjectNode){
                extraParams = (ObjectNode) event.get("extra_params");
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
            payloadToSend.put("extra_params", extraParams.toString());
        }
        if(app.getSound() != null && !app.getSound().isEmpty()){
            payloadToSend.put("sound", app.getSound());
        }
        ObjectNode aps = Json.newObject();
        int length = payloadToSend.toString().getBytes().length;
        while(length >= iosSize){
            msg = msg.substring(0, msg.length() - 1);
            payloadToSend.put("alert", msg);
            length = payloadToSend.toString().getBytes().length;
        }
        if(msg.isEmpty()){
            msg = event.get("msg").asText();
            msg = msg.substring(0, 20);
            payloadToSend.put("alert", msg);
        }

        aps.put("aps", payloadToSend);


        return aps;
    }
}
