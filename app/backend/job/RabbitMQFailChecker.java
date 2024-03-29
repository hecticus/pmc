package backend.job;

import akka.actor.Cancellable;
import backend.Constants;
import backend.HecticusThread;
import backend.ServerInstance;
import backend.rabbitmq.RabbitMQ;
import models.Config;
import models.basic.Event;
import utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 8/1/14.
 */
public class RabbitMQFailChecker extends HecticusThread {

    private int maxRetry;

    public RabbitMQFailChecker() {
        this.setActTime(System.currentTimeMillis());
        this.setInitTime(System.currentTimeMillis());
        this.setPrevTime(System.currentTimeMillis());
        //set name
        this.setName("RabbitMQFailChecker-" + System.currentTimeMillis());
    }

    public RabbitMQFailChecker(String name, AtomicBoolean run, Cancellable cancellable) {
        super("RabbitMQFailChecker-"+name, run, cancellable);
        maxRetry = Config.getInt("rabbitFC-max-retry");
    }

    public RabbitMQFailChecker(String name, AtomicBoolean run) {
        super("RabbitMQFailChecker-"+name, run);
        maxRetry = Config.getInt("rabbitFC-max-retry");
    }

    public RabbitMQFailChecker(AtomicBoolean run) {
        super("RabbitMQFailChecker",run);
        maxRetry = Config.getInt("rabbitFC-max-retry");
    }

    @Override
    public void process(Map args) {
        try{
            List<Event> events = Event.finder.where().eq("instance", ServerInstance.getInstance().getRealInstance()).findList();
            for(int i = 0; isAlive() && i < events.size(); ++i){
                Event e = events.get(i);
                String body = null;
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(e.getEvent()));
                    body = br.readLine();
                } catch (Exception ex) {
                } finally {
                    try {
                        if (br != null)br.close();
                    } catch (Exception ex) {

                    }
                }
                if(body != null && !body.isEmpty()){
                    boolean result = false;
                    if(e.getType().equalsIgnoreCase(Constants.EVENT)){
                        result = RabbitMQ.getInstance().insertEventLyraWithResult(body);
                    } else if(e.getType().equalsIgnoreCase(Constants.PUSH)){
                        result = RabbitMQ.getInstance().insertPushLyraWithResult(body);
                    } else if(e.getType().equalsIgnoreCase(Constants.RESULT)){
                        result = RabbitMQ.getInstance().insertPushResultLyraWithResult(body);
                    }
                    if(result){
                        File f = new File(e.getEvent());
                        Event.delete(e);
                        f.delete();
                    } else {
                        int retry = e.getRetry();
                        if(retry >= maxRetry) {
                            File f = new File(e.getEvent());
                            Event.delete(e);
                            f.delete();
                        } else {
                            retry++;
                            e.setRetry(retry);
                            Event.update(e);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Utils.printToLog(HecticusThread.class, "Error en el RabbitMQFailChecker", "Ocurrio un error en el RabbitMQFailChecker ", true, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }
}
