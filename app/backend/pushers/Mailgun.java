package backend.pushers;

import backend.Constants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hecticus.rackspacemailgun.MailGun;
import models.apps.Application;
import models.basic.Config;
import play.libs.Json;
import utils.Utils;

import java.util.Map;

/**
 * Created by plessmann on 21/07/15.
 */
public class Mailgun extends Pusher {

    public Mailgun() {
    }

    public Mailgun(boolean insertResult) {
        super(insertResult);
    }

    @Override
    public ObjectNode push(ObjectNode event, Application app) {
        ObjectNode fResponse = Json.newObject();
        String regIDs = event.get(Constants.REG_IDS).asText();
        String msg = event.get(Constants.MSG).asText();
        ObjectNode mail = (ObjectNode) event.get(getDevice().getName());
        String title = app.getTitle();
        if (!app.isDebug()) {
            try {
                if(mail.has(Constants.TITLE)){
                    title = mail.get(Constants.TITLE).asText();
                }
                if(mail.has(Constants.MESSAGE)){
                    msg = mail.get(Constants.MESSAGE).asText();
                }
                Map sendSimpleMessage = null;
                if(mail.has(Constants.HTML) && mail.get(Constants.HTML).asBoolean()){
                    sendSimpleMessage = MailGun.sendHtmlMessage(app.getMailgunFrom(), app.getMailgunTo(), null, regIDs, title, msg, app.getMailgunApikey(), app.getMailgunApiurl());
                } else {
                    sendSimpleMessage = MailGun.sendSimpleMessage(app.getMailgunFrom(), app.getMailgunTo(), null, regIDs, title, msg, app.getMailgunApikey(), app.getMailgunApiurl());
                }
                if (sendSimpleMessage != null && (Integer) sendSimpleMessage.get(Constants.STATUS) != 200) {
                    String emsg = "error en MailGun en el HecticusPusher, MailGun respondio " + sendSimpleMessage.toString();
                    fResponse = buildBasicResponse(1, emsg);
                    Utils.printToLog(this, "Error en el HecticusPusher", emsg, true, null, "support-level-1", Config.LOGGER_ERROR);
                }
            } catch (Throwable t) {
                String emsg = "Proceso continua. Error en el MailGun, puede ser falta de librerias de jersey o de oauth";
                fResponse = buildErrorResponse(-1, emsg, t);
                Utils.printToLog(this, "Error en el HecticusPusher", emsg, true, t, "support-level-1", Config.LOGGER_ERROR);
            }
        }
        return fResponse;
    }
}
