package backend.resolvers;

import backend.Constants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.basic.Config;
import play.libs.Json;

import java.net.URLDecoder;

/**
 * Created by plesse on 12/10/14.
 */
public class MailResolver extends Resolver {

    public MailResolver() {
    }

    @Override
    public ObjectNode resolve(ObjectNode event, Application app) {
        String msg = event.get(Constants.MSG).asText();
        boolean html = event.has(Constants.HTML) && event.get(Constants.HTML).asBoolean();
        try{
            msg = URLDecoder.decode(msg, Constants.ENCODING_UTF_8);
        } catch (Exception ex){
            return null;
        }
        ObjectNode message = Json.newObject();
        message.put(Constants.MESSAGE, msg);
        message.put(Constants.TITLE, app.getTitle());
        message.put(Constants.HTML, html);
        return message;
    }
}
