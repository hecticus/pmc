package backend.resolvers;

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
        String msg = event.get("msg").asText();
        boolean html = event.has("html") && event.get("html").asBoolean();
        try{
            msg = URLDecoder.decode(msg, "UTF-8");
        } catch (Exception ex){
            return null;
        }
        ObjectNode message = Json.newObject();
        message.put("message", msg);
        message.put("title", app.getTitle());
        message.put("html", html);
        return message;
    }
}
