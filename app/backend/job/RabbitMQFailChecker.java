package backend.job;

import akka.actor.Cancellable;
import backend.rabbitmq.RabbitMQ;
import models.basic.Config;
import models.basic.Event;
import utils.Utils;

import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 8/1/14.
 */
public class RabbitMQFailChecker extends HecticusThread {
    private int maxRetry;
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
    public void process() {
        try{
            List<Event> events = Event.finder.all();
            for(int i = 0; isAlive() && i < events.size(); ++i){
                Event e = events.get(i);
                String body = URLDecoder.decode(e.getEvent(),"UTF-8");
                boolean result = false;
                if(e.getType().equalsIgnoreCase("event")){
                    result = RabbitMQ.getInstance().insertEventLyraWithResult(body);
                } else if(e.getType().equalsIgnoreCase("push")){
                    result = RabbitMQ.getInstance().insertPushLyraWithResult(body);
                } else if(e.getType().equalsIgnoreCase("result")){
                    result = RabbitMQ.getInstance().insertPushResultLyraWithResult(body);
                }
                if(result){
                    Event.delete(e);
                } else {
                    int retry = e.getRetry();
                    if(retry >= maxRetry) {
                        Event.delete(e);
                    } else {
                        retry++;
                        e.setRetry(retry);
                        Event.update(e);
                    }
                }
            }
        } catch (Exception ex) {
            Utils.printToLog(HecticusThread.class, "Error en el RabbitMQFailChecker", "Ocurrio un error en el RabbitMQFailChecker ", true, ex, "support-level-1", Config.LOGGER_ERROR);
        }
    }
}
