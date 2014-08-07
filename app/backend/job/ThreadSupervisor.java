package backend.job;

import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import backend.apns.JavApns;
import backend.rabbitmq.RabbitMQ;
import models.basic.Config;
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
public class ThreadSupervisor extends HecticusThread {

    private ArrayList<HecticusThread> pConsumers = null;
    private ArrayList<HecticusThread> eConsumers = null;
    private ArrayList<HecticusThread> analyzers = null;
    private HecticusThread cacheLoader = null;
    private HecticusThread iosFeedbackChecker = null;
    private HecticusThread rabbitMQFailChecker = null;
    private ActorSystem system = null;

    public ThreadSupervisor(String name, AtomicBoolean run, Cancellable cancellable, ArrayList<HecticusThread> pConsumers, ArrayList<HecticusThread> eConsumers, ArrayList<HecticusThread> analyzers, ActorSystem system, HecticusThread cacheLoader, HecticusThread iosFeedbackChecker, HecticusThread rabbitMQFailChecker) {
        super("ThreadSupervisor-"+name, run, cancellable);
        this.pConsumers = pConsumers;
        this.eConsumers = eConsumers;
        this.analyzers = analyzers;
        this.system = system;
        this.cacheLoader = cacheLoader;
        this.iosFeedbackChecker = iosFeedbackChecker;
        this.rabbitMQFailChecker = rabbitMQFailChecker;
    }

    public ThreadSupervisor(String name, AtomicBoolean run, ArrayList<HecticusThread> pConsumers, ArrayList<HecticusThread> eConsumers, ArrayList<HecticusThread> analyzers, ActorSystem system, HecticusThread cacheLoader, HecticusThread iosFeedbackChecker, HecticusThread rabbitMQFailChecker) {
        super("ThreadSupervisor-"+name, run);
        this.pConsumers = pConsumers;
        this.eConsumers = eConsumers;
        this.analyzers = analyzers;
        this.system = system;
        this.cacheLoader = cacheLoader;
        this.iosFeedbackChecker = iosFeedbackChecker;
        this.rabbitMQFailChecker = rabbitMQFailChecker;
    }

    public ThreadSupervisor(AtomicBoolean run, ArrayList<HecticusThread> pConsumers, ArrayList<HecticusThread> eConsumers, ArrayList<HecticusThread> analyzers, ActorSystem system, HecticusThread cacheLoader, HecticusThread iosFeedbackChecker, HecticusThread rabbitMQFailChecker) {
        super("ThreadSupervisor", run);
        this.pConsumers = pConsumers;
        this.eConsumers = eConsumers;
        this.analyzers = analyzers;
        this.system = system;
        this.cacheLoader = cacheLoader;
        this.iosFeedbackChecker = iosFeedbackChecker;
        this.rabbitMQFailChecker = rabbitMQFailChecker;
    }

    public ThreadSupervisor(String name, AtomicBoolean run, Cancellable cancellable, ActorSystem system) {
        super("ThreadSupervisor-"+name, run, cancellable);
        this.system = system;
        init();
    }

    public ThreadSupervisor(String name, AtomicBoolean run, ActorSystem system) {
        super("ThreadSupervisor-"+name, run);
        this.system = system;
        init();
    }

    public ThreadSupervisor(AtomicBoolean run, ActorSystem system) {
        super("ThreadSupervisor",run);
        this.system = system;
        init();
    }

    /**
     * Metodo para monitorear los tiempos de ejecucion de los hilos del PMC
     */
    @Override
    public void process() {
        checkAliveThreads();
        checkEventConsumersQuantity();
        checkPushConsumersQuantity();
        checkResultConsumersQuantity();
        checkIosFeedbackChecker();
        checkCacheLoader();
        checkRabbitMQFailChecker();
    }

    private void checkRabbitMQFailChecker() {
        if(rabbitMQFailChecker == null && Utils.serverIp != null && Config.getInt("allow-RMQFC") == 1){
            Utils.printToLog(ThreadSupervisor.class, null, "Arrancando RabbitMQFailChecker", false, null, "support-level-1", Config.LOGGER_INFO);
            rabbitMQFailChecker = new RabbitMQFailChecker(getRun());
            Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(1, HOURS), rabbitMQFailChecker, system.dispatcher());
            rabbitMQFailChecker.setCancellable(cancellable);
        } else if(rabbitMQFailChecker != null && Config.getInt("allow-RMQFC") == 0){
            rabbitMQFailChecker.cancel();
            rabbitMQFailChecker = null;
        }
    }

    private void checkCacheLoader() {
        if(cacheLoader == null && Config.getInt("allow-CL") == 1){
            Utils.printToLog(ThreadSupervisor.class, null, "Arrancando CacheLoader", false, null, "support-level-1", Config.LOGGER_INFO);
            cacheLoader = new CacheLoader(getRun());
            Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(Config.getInt("cache-loader-sleep"), SECONDS), cacheLoader, system.dispatcher());
            cacheLoader.setCancellable(cancellable);
        } else if(cacheLoader != null && Config.getInt("allow-CL") == 0){
            cacheLoader.cancel();
            cacheLoader = null;
        }
    }

    private void checkIosFeedbackChecker() {
        if(iosFeedbackChecker == null && Config.getInt("allow-IOSFC") == 1){
            Utils.printToLog(ThreadSupervisor.class, null, "Arrancando IOSFeedbackChecker", false, null, "support-level-1", Config.LOGGER_INFO);
            iosFeedbackChecker = new IOSFeedbackChecker(getRun());
            Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(2, HOURS), iosFeedbackChecker, system.dispatcher());
            iosFeedbackChecker.setCancellable(cancellable);
        } else if(iosFeedbackChecker != null && Config.getInt("allow-IOSFC") == 0){
            iosFeedbackChecker.cancel();
            iosFeedbackChecker = null;
        }
    }

    private void checkResultConsumersQuantity() {
        int resultConsumers = Config.getInt("resultAnalyzers");
        if(resultConsumers > analyzers.size()){
            int toStart = resultConsumers - analyzers.size();
            Utils.printToLog(ThreadSupervisor.class, null, "Arrancando " + toStart + " PushManagers", false, null, "support-level-1", Config.LOGGER_INFO);
            for(int i = 0; isAlive() && i <  toStart; ++i){
                HecticusThread push = new ResultAnalyzer(getRun());
                Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(1, SECONDS), push, system.dispatcher());
                push.setCancellable(cancellable);
                analyzers.add(push);
            }
        } else if(resultConsumers < analyzers.size()){
            int toStop = analyzers.size() - resultConsumers;
            for(int i = 0; isAlive() && i <  toStop; ++i){
                HecticusThread result = analyzers.get(0);
                result.cancel();
                analyzers.remove(0);
            }
            Utils.printToLog(ThreadSupervisor.class, null, "Quedan " + analyzers.size() + " EventManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        }
    }

    private void checkPushConsumersQuantity() {
        int pushConsumers = Config.getInt("pushConsumers");
        if(pushConsumers > pConsumers.size()){
            int toStart = pushConsumers - pConsumers.size();
            Utils.printToLog(ThreadSupervisor.class, null, "Arrancando " + toStart + " PushManagers", false, null, "support-level-1", Config.LOGGER_INFO);
            for(int i = 0; isAlive() && i <  toStart; ++i){
                HecticusThread push = new PushManager(getRun());
                Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(1, SECONDS), push, system.dispatcher());
                push.setCancellable(cancellable);
                pConsumers.add(push);

            }
        } else if(pushConsumers < pConsumers.size()){
            int toStop = pConsumers.size() - pushConsumers;
            for(int i = 0; isAlive() && i <  toStop; ++i){
                HecticusThread push = pConsumers.get(0);
                push.cancel();
                pConsumers.remove(0);
            }
            Utils.printToLog(ThreadSupervisor.class, null, "Quedan " + pConsumers.size() + " EventManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        }
    }

    private void checkEventConsumersQuantity() {
        int eventConsumers = Config.getInt("eventConsumers");
        if(eventConsumers > eConsumers.size()){
            int toStart = eventConsumers - eConsumers.size();
            Utils.printToLog(ThreadSupervisor.class, null, "Arrancando " + toStart + " EventManagers", false, null, "support-level-1", Config.LOGGER_INFO);
            for(int i = 0; isAlive() && i <  toStart; ++i){
                HecticusThread event = new EventManager(getRun());
                Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(1, SECONDS), event, system.dispatcher());
                event.setCancellable(cancellable);
                eConsumers.add(event);
            }
        } else if(eventConsumers < eConsumers.size()){
            int toStop = eConsumers.size() - eventConsumers;
            for(int i = 0; isAlive() && i <  toStop; ++i){
                HecticusThread event = eConsumers.get(0);
                event.cancel();
                eConsumers.remove(0);
            }
            Utils.printToLog(ThreadSupervisor.class, null, "Quedan " + eConsumers.size() + " EventManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        }
    }

    private void init(){
        try {
            RabbitMQ.getInstance();
            JavApns.getInstance();
        } catch (Exception e) {
            Utils.printToLog(ThreadSupervisor.class, null, "Error instanciando RabbitMQ", false, null, "support-level-1", Config.LOGGER_INFO);
        }
        eConsumers = new ArrayList<HecticusThread>();
        pConsumers = new ArrayList<HecticusThread>();
        analyzers = new ArrayList<HecticusThread>();
        int eventConsumers = Config.getInt("eventConsumers");
        Utils.printToLog(ThreadSupervisor.class, null, "Arrancando " + eventConsumers + " EventManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        for(int i = 0; i < eventConsumers; ++i){
            HecticusThread event = new EventManager(getRun());
            Cancellable cancellable = system.scheduler().schedule(Duration.create(30, SECONDS), Duration.create(1, SECONDS), event, system.dispatcher());
            event.setCancellable(cancellable);
            eConsumers.add(event);
        }
        int pushConsumers = Config.getInt("pushConsumers");
        Utils.printToLog(ThreadSupervisor.class, null, "Arrancando " + pushConsumers + " PushManagers", false, null, "support-level-1", Config.LOGGER_INFO);
        for(int i = 0; i < pushConsumers; ++i){
            HecticusThread push = new PushManager(getRun());
            Cancellable cancellable = system.scheduler().schedule(Duration.create(30, SECONDS), Duration.create(1, SECONDS), push, system.dispatcher());
            push.setCancellable(cancellable);
            pConsumers.add(push);
        }

        int resultAnalyzers = Config.getInt("resultAnalyzers");
        Utils.printToLog(ThreadSupervisor.class, null, "Arrancando " + resultAnalyzers + " ResultAnalyzer", false, null, "support-level-1", Config.LOGGER_INFO);
        for(int i = 0; i < resultAnalyzers; ++i){
            HecticusThread analyzer = new ResultAnalyzer(getRun());
            Cancellable cancellable = system.scheduler().schedule(Duration.create(30, SECONDS), Duration.create(1, SECONDS), analyzer, system.dispatcher());
            analyzer.setCancellable(cancellable);
            analyzers.add(analyzer);
        }
        Cancellable cancellable = null;
        if(Config.getInt("allow-IOSFC") == 1){
            Utils.printToLog(ThreadSupervisor.class, null, "Arrancando IOSFeedbackChecker", false, null, "support-level-1", Config.LOGGER_INFO);
            iosFeedbackChecker = new IOSFeedbackChecker(getRun());
            cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(2, HOURS), iosFeedbackChecker, system.dispatcher());
            iosFeedbackChecker.setCancellable(cancellable);
        }
        if(Config.getInt("allow-CL") == 1){
            Utils.printToLog(ThreadSupervisor.class, null, "Arrancando CacheLoader", false, null, "support-level-1", Config.LOGGER_INFO);
            cacheLoader = new CacheLoader(getRun());
            cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(Config.getInt("cache-loader-sleep"), SECONDS), cacheLoader, system.dispatcher());
            cacheLoader.setCancellable(cancellable);
        }
        if(Utils.serverIp != null && Config.getInt("allow-RMQFC") == 1){
            Utils.printToLog(ThreadSupervisor.class, null, "Arrancando RabbitMQFailChecker", false, null, "support-level-1", Config.LOGGER_INFO);
            rabbitMQFailChecker = new RabbitMQFailChecker(getRun());
            cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(1, HOURS), rabbitMQFailChecker, system.dispatcher());
            rabbitMQFailChecker.setCancellable(cancellable);
        }
    }


    @Override
    public void stop() {
        if(eConsumers != null && !eConsumers.isEmpty()){
            for(HecticusThread ht : eConsumers){
                ht.cancel();
            }
            Utils.printToLog(ThreadSupervisor.class, null, "Apagados " + eConsumers.size() + " EventManagers", false, null, "support-level-1", Config.LOGGER_INFO);
            eConsumers.clear();
        }

        if(eConsumers != null && !eConsumers.isEmpty()){
            for(HecticusThread ht : pConsumers){
                ht.cancel();
            }
            Utils.printToLog(ThreadSupervisor.class, null, "Apagados " + pConsumers.size() + " PushManagers", false, null, "support-level-1", Config.LOGGER_INFO);
            pConsumers.clear();
        }

        if(eConsumers != null && !eConsumers.isEmpty()){
            for(HecticusThread ht : analyzers){
                ht.cancel();
            }
            Utils.printToLog(ThreadSupervisor.class, null, "Apagados " + analyzers.size() + " ResultAnalyzer", false, null, "support-level-1", Config.LOGGER_INFO);
            analyzers.clear();
        }

        if(cacheLoader != null){
            cacheLoader.getCancellable().cancel();
        }

        if(iosFeedbackChecker != null){
            iosFeedbackChecker.cancel();
        }

        if(rabbitMQFailChecker != null){
            rabbitMQFailChecker.cancel();
        }
//        try{RabbitMQ.getInstance().closeInstance();}catch(Exception ex){}
    }

    private void checkAliveThreads() {
        long allowedTime = Config.getLong("jobs-keep-alive-allowed");
        for(HecticusThread ht : pConsumers){
            long threadTime = ht.runningTime();
            if(isAlive() && ht.isActive() && threadTime > allowedTime){
                Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + ht.getName() + " lleva " + threadTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
            }
        }
        for(HecticusThread ht : eConsumers){
            long threadTime = ht.runningTime();
            if(isAlive() && ht.isActive() && threadTime > allowedTime){
                Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + ht.getName() + " lleva " + threadTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
            }
        }
        for(HecticusThread ht : analyzers){
            long threadTime = ht.runningTime();
            if(isAlive() && ht.isActive() && threadTime > allowedTime){
                Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + ht.getName() + " lleva " + threadTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
            }
        }
        if(cacheLoader != null){
            long cacheLoaderTime = cacheLoader.runningTime();
            if(cacheLoader.isActive() && cacheLoaderTime > allowedTime){
                Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + cacheLoader.getName() + " lleva " + cacheLoaderTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
            }
        }

        if(iosFeedbackChecker != null){
            long iosFCTime = iosFeedbackChecker.runningTime();
            if(iosFeedbackChecker.isActive() && iosFCTime > allowedTime){
                Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + iosFeedbackChecker.getName() + " lleva " + iosFCTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
            }
        }

        if(rabbitMQFailChecker != null){
            long rabbitMQFCTime = rabbitMQFailChecker.runningTime();
            if(rabbitMQFailChecker.isActive() && rabbitMQFCTime > allowedTime){
                Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + rabbitMQFailChecker.getName() + " lleva " + rabbitMQFCTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
            }
        }
    }
}
