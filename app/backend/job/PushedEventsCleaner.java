package backend.job;

import akka.actor.Cancellable;
import backend.HecticusThread;
import models.Config;
import models.basic.PushedEvent;
import utils.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 11/6/14.
 */
public class PushedEventsCleaner extends HecticusThread {

    public PushedEventsCleaner() {
        this.setActTime(System.currentTimeMillis());
        this.setInitTime(System.currentTimeMillis());
        this.setPrevTime(System.currentTimeMillis());
        //set name
        this.setName("PushedEventsCleaner-" + System.currentTimeMillis());
    }

    protected PushedEventsCleaner(String name, AtomicBoolean run, Cancellable cancellable) {
        super("PushedEventsCleaner-"+name, run, cancellable);
    }

    protected PushedEventsCleaner(String name, AtomicBoolean run) {
        super("PushedEventsCleaner-"+name, run);
    }

    protected PushedEventsCleaner(AtomicBoolean run) {
        super("PushedEventsCleaner", run);
    }

    @Override
    public void process(Map args) {
        try {
            TimeZone tz = TimeZone.getDefault();
            Calendar actualDate = new GregorianCalendar(tz);
            actualDate.add(Calendar.DAY_OF_MONTH, -Config.getInt("clean-window"));
            List<PushedEvent> eventsToDelete = PushedEvent.finder.where().le("time", actualDate.getTimeInMillis()).findList();
            for(PushedEvent pushedEvent : eventsToDelete) {
                pushedEvent.delete();
            }
        } catch (Exception e){
            Utils.printToLog(PushedEventsCleaner.class, "Error en el PushedEventsCleaner", "Ocurrio un error en el limpiando los PushedEvents ", true, e, "support-level-1", Config.LOGGER_ERROR);
        }
    }
}
