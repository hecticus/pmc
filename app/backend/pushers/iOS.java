package backend.pushers;

import backend.Constants;
import backend.apns.JavApns;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javapns.Push;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import models.apps.Application;
import models.basic.Config;
import play.libs.Json;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by plessmann on 21/07/15.
 */
public class iOS extends Pusher {



    public iOS() {
    }

    public iOS(boolean insertResult) {
        super(insertResult);
    }

    @Override
    public ObjectNode push(ObjectNode event, Application app) {
        ObjectNode fResponse = Json.newObject();
        String regIDs = event.get(Constants.REG_IDS).asText();
        String msg = event.get(Constants.MSG).asText();
        String[] registrationIds = regIDs.split(Constants.REG_IDS_SPLITTER);
        if(!app.isDebug()){
            try {
                PushedNotifications result = null;
                File cert  = new File((!app.isIosSandbox()? app.getIosPushApnsCertProduction() : app.getIosPushApnsCertSandbox()));
                if(app.getSound() != null && !app.getSound().isEmpty()){
                    result = Push.combined(msg, 0, app.getSound(), cert, app.getIosPushApnsPassphrase(), !app.isIosSandbox(), registrationIds);
                } else {
                    result = Push.alert(msg, cert, app.getIosPushApnsPassphrase(), !app.isIosSandbox(), registrationIds);
                }
                PushNotificationPayload payload = PushNotificationPayload.alert(msg);
                if(app.getSound() != null && !app.getSound().isEmpty()){
                    payload.addSound(app.getSound());
                }
                JavApns.getInstance().enqueue(app, payload, registrationIds);
                ArrayList<String> failedIds = new ArrayList<>();
                if(result != null){
                    for (PushedNotification notification : result) {
                        if(!notification.isSuccessful()) {
                            String invalidToken = notification.getDevice().getToken();
                            failedIds.add(invalidToken);
                        }
                    }
                    fResponse = buildBasicResponse(0, Constants.OK);
                } else {
                    fResponse = buildBasicResponse(1, String.format(Constants.ERROR_EXTRA, "Result es null"));
                }
                if(canInsertResult() && !failedIds.isEmpty()){
                    ObjectNode response = Json.newObject();
                    response.put(Constants.ORIGINAL_IDS, Json.toJson(registrationIds));
                    response.put(Constants.PUSH_TYPE, getDevice().getName());
                    response.put(Constants.FAILED_IDS, Json.toJson(failedIds));
                    long emTime = event.get(Constants.EM_TIME).asLong();
                    long prodTime = event.get(Constants.PROD_TIME).asLong();
                    long pmTime = event.get(Constants.PM_TIME).asLong();
                    long insertionTime = event.get(Constants.INSERTION_TIME).asLong();
                    response.put(Constants.EM_TIME, emTime);
                    response.put(Constants.PROD_TIME, prodTime);
                    response.put(Constants.PM_TIME, pmTime);
                    if(event.has(Constants.GENERATION_TIME)) {
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
            } catch (Exception e) {
                fResponse = buildErrorResponse(-1, "Error", e);
                Utils.printToLog(iOS.class, "Error en el HecticusPusher", "El ocurrio un error en el HecticusPusher procesando el evento: " + event.toString(), false, e, "support-level-1", Config.LOGGER_ERROR);
            }
        }
        return fResponse;
    }
}
