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
    ArrayList<HecticusThread> eConsumers = null;
    ArrayList<HecticusThread> pConsumers = null;
    ArrayList<HecticusThread> analyzers = null;
    HecticusThread cacheLoader = null;
    HecticusThread supervisor = null;
    HecticusThread iosFeedbackChecker = null;

    @Override
    public void onStart(Application application) {
        super.onStart(application);
        Utils.printToLog(Global.class, null, "Arrancando " + Config.getString("app-name"), false, null, "support-level-1", Config.LOGGER_INFO);
        ActorSystem system = ActorSystem.create("application");

        run = new AtomicBoolean(true);
        Utils.run = run;
        eConsumers = new ArrayList<HecticusThread>();
        pConsumers = new ArrayList<HecticusThread>();
        analyzers = new ArrayList<HecticusThread>();
        int eventConsumers = Config.getInt("eventConsumers");
        Utils.printToLog(Global.class, null, "Arrancando " + eventConsumers + " EventManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        for(int i = 0; i < eventConsumers; ++i){
            HecticusThread event = new EventManager(run);
            Cancellable cancellable = system.scheduler().schedule(Duration.create(20, SECONDS), Duration.create(1, SECONDS), event, system.dispatcher());
            event.setCancellable(cancellable);
            eConsumers.add(event);
        }
        int pushConsumers = Config.getInt("pushConsumers");
        Utils.printToLog(Global.class, null, "Arrancando " + pushConsumers + " PushManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        for(int i = 0; i < pushConsumers; ++i){
            HecticusThread push = new PushManager(run);
            Cancellable cancellable = system.scheduler().schedule(Duration.create(25, SECONDS), Duration.create(1, SECONDS), push, system.dispatcher());
            push.setCancellable(cancellable);
            pConsumers.add(push);
        }

        int resultAnalyzers = Config.getInt("resultAnalyzers");
        Utils.printToLog(Global.class, null, "Arrancando " + resultAnalyzers + " ResultAnalyzer", false, null, "support-level-1", Config.LOGGER_INFO);
        for(int i = 0; i < resultAnalyzers; ++i){
            HecticusThread analyzer = new ResultAnalyzer(run);
            Cancellable cancellable = system.scheduler().schedule(Duration.create(25, SECONDS), Duration.create(1, SECONDS), analyzer, system.dispatcher());
            analyzer.setCancellable(cancellable);
            analyzers.add(analyzer);
        }

        Utils.printToLog(Global.class, null, "Arrancando IOSFeedbackChecker", false, null, "support-level-1", Config.LOGGER_INFO);
        iosFeedbackChecker = new IOSFeedbackChecker(run);
        Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(2, HOURS), iosFeedbackChecker, system.dispatcher());
        iosFeedbackChecker.setCancellable(cancellable);

        Utils.printToLog(Global.class, null, "Arrancando CacheLoader", false, null, "support-level-1", Config.LOGGER_INFO);
        cacheLoader = new CacheLoader(run);
        cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(Config.getInt("cache-loader-sleep"), SECONDS), cacheLoader, system.dispatcher());
        cacheLoader.setCancellable(cancellable);

        Utils.printToLog(Global.class, null, "Arrancando ThreadSupervisor", false, null, "support-level-1", Config.LOGGER_INFO);
        supervisor = new ThreadSupervisor(run, pConsumers, eConsumers, analyzers, system, cacheLoader, iosFeedbackChecker);
        cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(5, MINUTES), supervisor, system.dispatcher());
        supervisor.setCancellable(cancellable);
    }

    @Override
    public void onStop(Application application) {
        super.onStop(application);
        run.set(false);
        Utils.printToLog(Global.class, null, "Apagando EventManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        for(HecticusThread ht : eConsumers){
            Cancellable cancellable = ht.getCancellable();
            if(ht.isActive() && !cancellable.isCancelled()){
                cancellable.cancel();
            }
        }
        Utils.printToLog(Global.class, null, "Apagados " + eConsumers.size() + " EventManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        eConsumers.clear();
        Utils.printToLog(Global.class, null, "Apagando PushManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        for(HecticusThread ht : pConsumers){
            Cancellable cancellable = ht.getCancellable();
            if(ht.isActive() && !cancellable.isCancelled()){
                cancellable.cancel();
            }
        }
        Utils.printToLog(Global.class, null, "Apagados " + pConsumers.size() + " PushManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        pConsumers.clear();
        Utils.printToLog(Global.class, null, "Apagando ResultAnalyzer", false, null, "support-level-1", Config.LOGGER_INFO);
        for(HecticusThread ht : analyzers){
            Cancellable cancellable = ht.getCancellable();
            if(ht.isActive() && !cancellable.isCancelled()){
                cancellable.cancel();
            }
        }
        Utils.printToLog(Global.class, null, "Apagados " + analyzers.size() + " ResultAnalyzer", false, null, "support-level-1", Config.LOGGER_INFO);
        analyzers.clear();
        Utils.printToLog(Global.class, null, "Apagando cacheLoader", false, null, "support-level-1", Config.LOGGER_INFO);
        Cancellable cancellablecl = cacheLoader.getCancellable();
        if(cacheLoader != null && cacheLoader.isActive() && !cancellablecl.isCancelled()){
            cancellablecl.cancel();
        }
        Utils.printToLog(Global.class, null, "Apagado cacheLoader", false, null, "support-level-1", Config.LOGGER_INFO);
        Utils.printToLog(Global.class, null, "Apagando ThreadSupervisor", false, null, "support-level-1", Config.LOGGER_INFO);
        Cancellable cancellable = supervisor.getCancellable();
        if(supervisor != null && supervisor.isActive() && !cancellable.isCancelled()){
            cancellable.cancel();
        }
        Utils.printToLog(Global.class, null, "Apagado ThreadSupervisor", false, null, "support-level-1", Config.LOGGER_INFO);
        eConsumers.clear();
        pConsumers.clear();
//        try{RabbitMQ.getInstance().closeInstance();}catch(Exception ex){}
    }
}
