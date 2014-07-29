package backend.job;

import akka.actor.Cancellable;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javapns.Push;
import javapns.devices.Device;
import models.apps.Application;
import models.basic.Config;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 7/25/14.
 */
public class IOSFeedbackChecker extends HecticusThread {

    public IOSFeedbackChecker(String name, AtomicBoolean run, Cancellable cancellable) {
        super("IOSFeedbackChecker-"+name, run, cancellable);
    }

    public IOSFeedbackChecker(String name, AtomicBoolean run) {
        super("IOSFeedbackChecker-"+name, run);
    }

    public IOSFeedbackChecker(AtomicBoolean run) {
        super("IOSFeedbackChecker", run);
    }

    @Override
    public void process() {
        try{
            List<Application> apps = Application.finder.all();
            for(int i = 0; isAlive() && i < apps.size(); ++i){
                checkIOSFeedback(apps.get(i));
            }
        } catch(Exception e) {
            Utils.printToLog(IOSFeedbackChecker.class, "", "Ocurrio un error en el IOSFeedbackChecker ", false, e, "support-level-1", Config.LOGGER_ERROR);
        }

    }

    private void checkIOSFeedback(Application application) {
        try{
            if(application.getActive() == 1 && application.getDebug() == 0 && application.getIosPushApnsCertProduction() != null && !application.getIosPushApnsCertProduction().isEmpty() && application.getIosPushApnsCertSandbox() != null && !application.getIosPushApnsCertSandbox().isEmpty() && application.getIosPushApnsPassphrase() != null && !application.getIosPushApnsPassphrase().isEmpty()){
//                File cert  = new File(Play.application().path().getAbsolutePath() + "/" + (application.getIosSandbox() == 0 ? application.getIosPushApnsCertProduction() : application.getIosPushApnsCertSandbox()));
                File cert  = new File((application.getIosSandbox() == 0 ? application.getIosPushApnsCertProduction() : application.getIosPushApnsCertSandbox()));
                List<Device> devices = Push.feedback(cert, application.getIosPushApnsPassphrase(), application.getIosSandbox() == 0);
                ArrayList<ObjectNode> toClean = new ArrayList<>();
                for (Device device : devices) {
                    ObjectNode operation = Json.newObject();
                    operation.put("operation", "DELETE");
                    operation.put("actual_id", device.getToken());
                    operation.put("type", "ios");
                    toClean.add(operation);
                }
                ObjectNode operations = Json.newObject();
                operations.put("operations", Json.toJson(toClean));
                try {
                    F.Promise<WSResponse> resultWS = WS.url(application.getCleanDeviceUrl()).post(operations);
                    ObjectNode response = (ObjectNode)resultWS.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
                } catch (Exception ex) {
                    Utils.printToLog(IOSFeedbackChecker.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
                }
            }
        } catch(Exception e) {
            Utils.printToLog(IOSFeedbackChecker.class, "", "Ocurrio un error en el IOSFeedbackChecker ", false, e, "support-level-1", Config.LOGGER_ERROR);
        }
    }


}
