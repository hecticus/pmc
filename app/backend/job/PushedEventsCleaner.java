package backend.job;

import akka.actor.Cancellable;
import models.basic.Config;
import models.basic.PushedEvent;
import utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 11/6/14.
 */
public class PushedEventsCleaner extends HecticusThread {

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
    public void process() {
        try {
            TimeZone tz = TimeZone.getDefault();
            Calendar actualDate = new GregorianCalendar(tz);
            actualDate.add(Calendar.DAY_OF_MONTH, -Config.getInt("clean-window"));
            List<PushedEvent> eventsToDelete = PushedEvent.finder.where().le("time", actualDate.getTimeInMillis()).findList();

//            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
//            System.out.println("a borrar = " + eventsToDelete.size() + " " + sf.format(actualDate.getTime()) + " " + actualDate.getTimeInMillis());

            for(PushedEvent pushedEvent : eventsToDelete) {
                pushedEvent.delete();
            }
        } catch (Exception e){
            Utils.printToLog(PushedEventsCleaner.class, "Error en el PushedEventsCleaner", "Ocurrio un error en el limpiando los PushedEvents ", true, e, "support-level-1", Config.LOGGER_ERROR);
        }
    }
}
