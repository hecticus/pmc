import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import backend.job.*;
import backend.job.test.HecticusGenerator;
//import backend.pushy.PushyManager;
import backend.rabbitmq.RabbitMQ;
import models.basic.Config;
import play.Application;
import play.GlobalSettings;
import scala.concurrent.duration.Duration;
import utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by plesse on 7/10/14.
 */
public class Global extends GlobalSettings {

    public static AtomicBoolean run = null;
    HecticusThread supervisor = null;

    @Override
    public void onStart(Application application) {
        super.onStart(application);
        Utils.printToLog(Global.class, null, "Arrancando " + Config.getString("app-name"), false, null, "support-level-1", Config.LOGGER_INFO);
        ActorSystem system = ActorSystem.create("application");
        run = new AtomicBoolean(true);
        Utils.run = run;
        Utils.printToLog(Global.class, null, "Arrancando ThreadSupervisor", false, null, "support-level-1", Config.LOGGER_INFO);
        supervisor = new ThreadSupervisor(run, system);
        Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(5, MINUTES), supervisor, system.dispatcher());
        supervisor.setCancellable(cancellable);
    }

    @Override
    public void onStop(Application application) {
        super.onStop(application);
        run.set(false);
        Utils.printToLog(Global.class, "Apagando " + Config.getString("app-name"), "Apagando " + Config.getString("app-name")+", se recibio la señal de shutdown", true, null, "support-level-1", Config.LOGGER_INFO);
        supervisor.cancel();
    }
}
