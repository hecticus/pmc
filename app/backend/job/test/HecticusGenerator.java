package backend.job.test;

import akka.actor.Cancellable;
import backend.HecticusThread;
import backend.rabbitmq.RabbitMQ;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Config;
import play.libs.Json;
import utils.Utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 7/11/14.
 */
public class HecticusGenerator extends HecticusThread {

    public HecticusGenerator(String name, AtomicBoolean run, Cancellable cancellable) {
        super("HecticusGenerator"+name, run, cancellable);
    }

    public HecticusGenerator(String name, AtomicBoolean run) {
        super("HecticusGenerator"+name, run);
    }

    public HecticusGenerator(AtomicBoolean run) {
        super("HecticusGenerator",run);
    }

    @Override
    public void process(Map args) {
        try{
            Random rand = new Random();
            int n = rand.nextInt(5000) + 1;
            ObjectNode event = Json.newObject();
            event.put("app", 1);
//            String msg = getName() + "_"+n+"_" + System.currentTimeMillis();
            String msg = "Prueba del PMC!!!";
            event.put("msg", msg);
            ArrayList<String> clients = new ArrayList<>(n);
//            for(int i = 0; i < n; ++i){
//                int k = i;//(rand.nextInt(10000) + 1) * i;
//                clients.add(""+k);
//            }
            clients.add("11");//juan
            clients.add("31");//shei
            clients.add("52637");//angela
            clients.add("19358");//capi
            clients.add("8417");//joe
            clients.add("13283");//afonso
            clients.add("13291");//inaki
            clients.add("2935");//christian
            clients.add("52638");//ronald
            clients.add("52639");//yule
            event.put("clients", Json.toJson(clients));
            event.put("insertionTime", System.currentTimeMillis());
            RabbitMQ.getInstance().insertEventLyra(event.toString());
//            Utils.printToLog(HecticusGenerator.class, null, "Generado el evento " + msg, false, null, "support-level-1", Config.LOGGER_INFO);
        } catch (Exception ex) {
            Utils.printToLog(HecticusGenerator.class, null, "error", false, ex, "support-level-1", Config.LOGGER_INFO);
        }
    }

}
