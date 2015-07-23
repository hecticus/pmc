package backend.pushers;

import backend.Constants;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.basic.Config;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by plessmann on 21/07/15.
 */
public class GCM2 extends Pusher {

    public GCM2() {
    }

    public GCM2(boolean insertResult) {
        super(insertResult);
    }

    @Override
    public ObjectNode push(ObjectNode event, Application app) {
        ObjectNode fResponse = null;
        Map params = getParams();
        if(params != null && !params.isEmpty()) {
            String regIDs = event.get(Constants.REG_IDS).asText();
            String androidPushUrl = (String) params.get(Constants.ANDROID_PUSH_URL);
            String[] registrationIds = regIDs.split(Constants.REG_IDS_SPLITTER);
            ObjectNode gcm = (ObjectNode) event.get(getDevice().getName());
            gcm.put(Constants.ANDROID_REGISTRATION_IDS, Json.toJson(registrationIds));
            if (!app.isDebug()) {
                F.Promise<WSResponse> result = WS.url(androidPushUrl).setContentType(Constants.ANDROID_CONTENT_TYPE).setHeader(Constants.ANDROID_AUTHORIZATION, String.format(Constants.ANDROID_KEY, app.getGoogleApiKey())).post(gcm);
                WSResponse r = null;
                try {
                    r = result.get(Config.getLong("external-ws-timeout-millis"), TimeUnit.MILLISECONDS);
                    int status = r.getStatus();
                    if (status == 200) {
                        ObjectNode response = (ObjectNode) r.asJson();
                        if (canInsertResult() && (response.has(Constants.ANDROID_CANONICAL_IDS) || (response.has(Constants.ANDROID_FAILURE) && response.get(Constants.ANDROID_FAILURE).asInt() > 0))) {
                            response.put(Constants.ORIGINAL_IDS, Json.toJson(registrationIds));
                            response.put(Constants.PUSH_TYPE, getDevice().getName());
                            long emTime = event.get(Constants.EM_TIME).asLong();
                            long prodTime = event.get(Constants.PROD_TIME).asLong();
                            long pmTime = event.get(Constants.PM_TIME).asLong();
                            long insertionTime = event.get(Constants.INSERTION_TIME).asLong();
                            response.put(Constants.EM_TIME, emTime);
                            response.put(Constants.PROD_TIME, prodTime);
                            response.put(Constants.PM_TIME, pmTime);
                            if (event.has(Constants.GENERATION_TIME)) {
                                response.put(Constants.GENERATION_TIME, event.get(Constants.GENERATION_TIME).asLong());
                            }
                            response.put(Constants.INSERTION_TIME, insertionTime);
                            response.put(Constants.APP, app.getIdApp());
                            try {
                                RabbitMQ.getInstance().insertPushResultLyra(response.toString());
                            } catch (Exception e) {
                                String emsg = "Proceso continua. Error insertando resultado de push en rabbit, response = " + response.toString();
                                Utils.printToLog(this, "Error en el HecticusPusher", emsg, true, e, "support-level-1", Config.LOGGER_ERROR);
                            }
                        }
                        fResponse = buildBasicResponse(0, Constants.OK, response);
                    } else {
                        fResponse = buildBasicResponse(1, "Error en la respuesta de Google, status: " + status);
                        Utils.printToLog(GCM2.class, null, "Error en la respuesta de Google, status: " + status, false, null, "support-level-1", Config.LOGGER_ERROR);
                    }
                } catch (Exception e) {
                    fResponse = buildErrorResponse(-1, Constants.ERROR, e);
                    Utils.printToLog(GCM2.class, null, "Error llamando a Google", false, e, "support-level-1", Config.LOGGER_ERROR);
                }
            }
        } else {
            fResponse = buildBasicResponse(2, String.format(Constants.ERROR_EXTRA, "Se necesitan parametros para este Pusher"));
        }
        return fResponse;
    }
}
