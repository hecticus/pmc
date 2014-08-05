package controllers.apps;

import backend.job.HecticusProducer;
import backend.job.HecticusThread;
import backend.job.ThreadSupervisor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.HecticusController;
import models.apps.Application;
import models.basic.Config;
import play.api.DefaultGlobal;
import play.libs.F;
import play.libs.F.*;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import utils.Utils;

import javax.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 7/11/14.
 */
public class AppsWS extends HecticusController {

    public static Result update(Long idApp){
        ObjectNode result = Json.newObject();
        ObjectNode jsonInfo = getJson();
        if(jsonInfo == null){
            result.put(Config.ERROR_KEY, -99);
            result.put(Config.DESCRIPTION_KEY, "No hay data de json");
            return badRequest(result);
        }
        try{
            int error = 0;
            String description = "OK";
            result.put(Config.ERROR_KEY, error);
            result.put(Config.DESCRIPTION_KEY, description);
            Application app = Application.finder.byId(idApp);
            if(app != null){
                if(jsonInfo.has("debug")){
                    app.setDebug(jsonInfo.get("debug").asInt());
                }
                if(jsonInfo.has("active")){
                    app.setActive(jsonInfo.get("active").asInt());
                }
                if(jsonInfo.has("batch-url")){
                    app.setBatchClientsUrl(jsonInfo.get("batch-url").asText());
                }
                if(jsonInfo.has("google-apikey")){
                    app.setGoogleApiKey(jsonInfo.get("google-apikey").asText());
                }
                if(jsonInfo.has("apn-cert-production")){
                    app.setIosPushApnsCertProduction(jsonInfo.get("apn-cert-production").asText());
                }
                if(jsonInfo.has("apn-cert-sandbox")){
                    app.setIosPushApnsCertSandbox(jsonInfo.get("apn-cert-sandbox").asText());
                }
                if(jsonInfo.has("apn-pass")){
                    app.setIosPushApnsPassphrase(jsonInfo.get("apn-pass").asText());
                }
                if(jsonInfo.has("device-url")){
                    app.setCleanDeviceUrl(jsonInfo.get("device-url").asText());
                }
                if(jsonInfo.has("mailgun-apikey")){
                    app.setMailgunApikey(jsonInfo.get("mailgun-apikey").asText());
                }
                if(jsonInfo.has("mailgun-apiurl")){
                    app.setMailgunApiurl(jsonInfo.get("mailgun-apiurl").asText());
                }
                if(jsonInfo.has("mailgun-from")){
                    app.setMailgunFrom(jsonInfo.get("mailgun-from").asText());
                }
                if(jsonInfo.has("mailgun-to")){
                    app.setMailgunTo(jsonInfo.get("mailgun-to").asText());
                }
                if(jsonInfo.has("name")){
                    app.setName(jsonInfo.get("name").asText());
                }
                if(jsonInfo.has("single-url")){
                    app.setSingleClientUrl(jsonInfo.get("single-url").asText());
                }
                if(jsonInfo.has("sound")){
                    app.setSound(jsonInfo.get("sound").asText());
                }
                if(jsonInfo.has("title")){
                    app.setTitle(jsonInfo.get("title").asText());
                }
                app.update();
                result.put(Config.RESPONSE_KEY, app.toJson());
            } else {
                result.put(Config.RESPONSE_KEY, "no such app " + idApp);
            }
        } catch (PersistenceException ex) {
            Utils.printToLog(AppsWS.class, "", "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
            result.put(Config.ERROR_KEY, 6);
            result.put(Config.DESCRIPTION_KEY,ex.getMessage());
        } catch (Exception ex) {
            Utils.printToLog(AppsWS.class, "", "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
            result.put(Config.ERROR_KEY, 1);
            result.put(Config.DESCRIPTION_KEY,ex.getMessage());
        } finally{
            if(result.get(Config.ERROR_KEY).asInt() == 0){
                return ok(result);
            }
            return badRequest(result);
        }
    }







}
