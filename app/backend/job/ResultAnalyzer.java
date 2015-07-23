package backend.job;

import akka.actor.Cancellable;
import backend.Constants;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.AppDevice;
import models.apps.Application;
import models.apps.Cleaner;
import models.basic.Config;
import play.libs.Json;
import utils.Utils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase para consumir los eventos de PUSH_RESULT y hacer la operacion necesaria para mantener limpia la tabla de RegistrationIDs
 *
 * Created by plesse on 7/25/14.
 */
public class ResultAnalyzer extends HecticusThread {
    public ResultAnalyzer(String name, AtomicBoolean run, Cancellable cancellable) {
        super("ResultAnalyzer-"+name, run, cancellable);
    }

    public ResultAnalyzer(String name, AtomicBoolean run) {
        super("ResultAnalyzer-"+name, run);
    }

    public ResultAnalyzer(AtomicBoolean run) {
        super("ResultAnalyzer", run);
    }

    /**
     * Metodo que consume eventos de PUSH_RESULT y dependiendo de su type los trata
     */
    @Override
    public void process() {
        try{
            String eventString = RabbitMQ.getInstance().getNextPushResultLyra();
            if(eventString != null){
                ObjectNode event = (ObjectNode) Json.parse(eventString);
                String type = event.get(Constants.PUSH_TYPE).asText();
                long appID = event.get(Constants.APP).asLong();
                Application app = Application.finder.byId(appID);
                if(app != null) {
                    AppDevice device = app.getDevice(type);
                    if(device != null){
                        Cleaner cleanerModel = device.getCleaner();
                        if(cleanerModel != null){
                            String className = cleanerModel.getClassName();
                            if(className != null && !className.isEmpty()){
                                Class cleanerClassName = Class.forName(className.trim());
                                if (cleanerClassName != null) {
                                    backend.cleaners.Cleaner cleaner = (backend.cleaners.Cleaner) cleanerClassName.newInstance();
                                    if (cleaner != null) {
                                        cleaner.setDevice(device.getDev());
                                        cleaner.clean(app, event, this);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Utils.printToLog(ResultAnalyzer.class, null, "error", false, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }
}
