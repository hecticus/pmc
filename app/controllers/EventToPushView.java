package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.AppDevice;
import models.apps.Device;
import models.basic.Config;
import models.basic.EventToPush;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;

import static play.data.Form.form;

import utils.Utils;
import views.html.events.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by plesse on 11/5/14.
 */
public class EventToPushView extends HecticusController {

    final static Form<EventToPush> EventToPushViewForm = form(EventToPush.class);
    public static Result GO_HOME = redirect(routes.EventToPushView.blank());


    @Restrict(@Group(Application.USER_ROLE))
    public static Result index() {
        return GO_HOME;
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result blank() {
        return ok(form.render(EventToPushViewForm));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result submit() throws IOException {
        Form<EventToPush> filledForm = EventToPushViewForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            return badRequest(form.render(filledForm));
        }
        EventToPush eventToPush = filledForm.get();
        models.apps.Application application = models.apps.Application.finder.byId(eventToPush.getApplication().getIdApp());
        if(application == null) {
            return badRequest(form.render(filledForm));
        }
        ObjectNode event = eventToPush.toJson();
        String url = null;
        if(filledForm.data().containsKey("all") && eventToPush.getAll()){
            int index = 0;
            boolean done = false;
            int batchSize = Config.getInt("core-query-limit");
            ArrayList<String> clients = new ArrayList<>();
            while (!done) {
                try {
                    F.Promise<WSResponse> result = WS.url(application.getBatchClientsUrl() + "/" + index + "/" + batchSize).get();
                    ObjectNode response = (ObjectNode) result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
                    if ((response != null) && (!Utils.checkIfResponseIsError(response))) {
                        done = true;
                        Iterator<JsonNode> clientsIterator = response.get("response").elements();
                        while(clientsIterator.hasNext()){
                            done = false;
                            ObjectNode actualClient = (ObjectNode) clientsIterator.next();
                            clients.add(actualClient.get("idClient").asText());
                        }
                    }
                } catch(Exception e) {
                    Utils.printToLog(EventToPushView.class, null, "Error cargando clientes a la cache, app: " + application.getIdApp() + " llamada: " + application.getBatchClientsUrl() + "/" + index + "/" + batchSize, false, e, "support-level-1", Config.LOGGER_ERROR);
                }
                index+=batchSize;
            }
            System.out.println(clients.isEmpty() + " " + clients.size());
            if(!clients.isEmpty()){
                url = "http://" + Config.getDaemonHost() + "/events/v1/insert";
                event.remove("regIDs");
                event.put("clients", Json.toJson(clients));
            }
        } else {
            Device device = Device.finder.where().eq("name", eventToPush.getType().intValue() == 0?"droid":"ios").findUnique();
            AppDevice allow = AppDevice.finder.where().eq("app.idApp", application.getIdApp()).eq("dev.idDevice", device.getIdDevice()).findUnique();
            if(allow != null){
                url = "http://" + Config.getDaemonHost() + "/push/" + application.getIdApp() + "/" + eventToPush.getType().intValue();
            }
        }
        if(url != null){
            F.Promise<WSResponse> result = WS.url(url).setContentType("application/json").post(event);
            ObjectNode fResponse = Json.newObject();
            WSResponse r = null;
            String resp = null;
            try{
                r = result.get(Config.getLong("external-ws-timeout-millis"), TimeUnit.MILLISECONDS);
                ObjectNode response = (ObjectNode) r.asJson();
                resp = response.toString();
                fResponse.put("response", Json.toJson(response));
            } catch(Exception e){
                try{
                    resp = r.asXml().toString();
                } catch(Exception e1){
                }
                fResponse.put("response", "Error pushing " + resp + " exception: " + e.getMessage());
            }
        }
        flash("success", Messages.get("events.java.pushed", eventToPush.getMessage()));
        return GO_HOME;
    }
}
