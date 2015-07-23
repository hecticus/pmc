package backend.pushers;

import backend.Constants;
import backend.apns.JavApns;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javapns.notification.PushNotificationPayload;
import models.apps.Application;
import models.basic.Config;
import play.libs.Json;
import utils.Utils;

/**
 * Created by plessmann on 21/07/15.
 */
public class iOSPooled extends Pusher {

    public iOSPooled() {
    }

    public iOSPooled(boolean insertResult) {
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
                ObjectNode apns = (ObjectNode) event.get(getDevice().getName());
                PushNotificationPayload payload = PushNotificationPayload.fromJSON(apns.toString());
                JavApns.getInstance().enqueue(app, payload, registrationIds);
                fResponse = buildBasicResponse(0, Constants.OK, apns);
            } catch (Exception e) {
                fResponse = buildErrorResponse(-1, Constants.ERROR, e);
                Utils.printToLog(iOSPooled.class, "Error en el HecticusPusher", "El ocurrio un error en el HecticusPusher procesando el evento: " + event.toString(), false, e, "support-level-1", Config.LOGGER_ERROR);
            }
        }
        return fResponse;
    }
}
