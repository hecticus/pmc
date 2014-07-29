package backend.job;

import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import models.basic.Config;
import scala.concurrent.duration.Duration;
import utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private ActorSystem system = null;

    public ThreadSupervisor(String name, AtomicBoolean run, Cancellable cancellable, ArrayList<HecticusThread> pConsumers, ArrayList<HecticusThread> eConsumers, ArrayList<HecticusThread> analyzers, ActorSystem system, HecticusThread cacheLoader, HecticusThread iosFeedbackChecker) {
        super("ThreadSupervisor-"+name+"-"+System.currentTimeMillis(), run, cancellable);
        this.pConsumers = pConsumers;
        this.eConsumers = eConsumers;
        this.analyzers = analyzers;
        this.system = system;
        this.cacheLoader = cacheLoader;
        this.iosFeedbackChecker = iosFeedbackChecker;
    }

    public ThreadSupervisor(String name, AtomicBoolean run, ArrayList<HecticusThread> pConsumers, ArrayList<HecticusThread> eConsumers, ArrayList<HecticusThread> analyzers, ActorSystem system, HecticusThread cacheLoader, HecticusThread iosFeedbackChecker) {
        super("ThreadSupervisor-"+name+"-"+System.currentTimeMillis(), run);
        this.pConsumers = pConsumers;
        this.eConsumers = eConsumers;
        this.analyzers = analyzers;
        this.system = system;
        this.cacheLoader = cacheLoader;
        this.iosFeedbackChecker = iosFeedbackChecker;
    }

    public ThreadSupervisor(AtomicBoolean run, ArrayList<HecticusThread> pConsumers, ArrayList<HecticusThread> eConsumers, ArrayList<HecticusThread> analyzers, ActorSystem system, HecticusThread cacheLoader, HecticusThread iosFeedbackChecker) {
        super("ThreadSupervisor-"+System.currentTimeMillis(), run);
        this.pConsumers = pConsumers;
        this.eConsumers = eConsumers;
        this.analyzers = analyzers;
        this.system = system;
        this.cacheLoader = cacheLoader;
        this.iosFeedbackChecker = iosFeedbackChecker;
    }

    @Override
    public void process() {
        long allowedTime = Config.getLong("jobs-keep-alive-allowed");
        for(HecticusThread ht : pConsumers){
            setAlive();
            long threadTime = ht.runningTime();
            if(ht.isActive() && threadTime > allowedTime){
                Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + ht.getName() + " lleva " + threadTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
            }
        }
        for(HecticusThread ht : eConsumers){
            setAlive();
            long threadTime = ht.runningTime();
            if(ht.isActive() && threadTime > allowedTime){
                Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + ht.getName() + " lleva " + threadTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
            }
        }
        for(HecticusThread ht : analyzers){
            setAlive();
            long threadTime = ht.runningTime();
            if(ht.isActive() && threadTime > allowedTime){
                Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + ht.getName() + " lleva " + threadTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
            }
        }
        long cacheLoaderTime = cacheLoader.runningTime();
        if(cacheLoader.isActive() && cacheLoaderTime > allowedTime){
            Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + cacheLoader.getName() + " lleva " + cacheLoaderTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
        }

        long iosFCTime = iosFeedbackChecker.runningTime();
        if(iosFeedbackChecker.isActive() && iosFCTime > allowedTime){
            Utils.printToLog(ThreadSupervisor.class, "Job Bloqueado", "El job " + iosFeedbackChecker.getName() + " lleva " + iosFCTime + " sin pasar por un setAlive()", false, null, "support-level-1", Config.LOGGER_ERROR);
        }

        int eventConsumers = Config.getInt("eventConsumers");
        int pushConsumers = Config.getInt("pushConsumers");
        if((eventConsumers != eConsumers.size()) || (pushConsumers != pConsumers.size())){
            if(eventConsumers > eConsumers.size()){
                Utils.printToLog(ThreadSupervisor.class, null, "Apagando jobs", false, null, "support-level-1", Config.LOGGER_ERROR);
                int toStart = eventConsumers - eConsumers.size();
                for(int i = 0; isAlive() && i <  toStart; ++i){
                    HecticusThread event = new EventManager(getRun());
                    Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(1, SECONDS), event, system.dispatcher());
                    event.setCancellable(cancellable);
                    eConsumers.add(event);
                }
            } else if(eventConsumers < eConsumers.size()){
                int toStop = eConsumers.size() - eventConsumers;
                Utils.printToLog(ThreadSupervisor.class, null, eConsumers.size() + " - " + eventConsumers + " = " + toStop, false, null, "support-level-1", Config.LOGGER_ERROR);
                for(int i = 0; isAlive() && i <  toStop; ++i){
                    HecticusThread event = eConsumers.get(i);
                    if(event.isActive()){
                        event.getCancellable().cancel();
                    }
                    Utils.printToLog(ThreadSupervisor.class, null, " apagando " + event.getName(), false, null, "support-level-1", Config.LOGGER_ERROR);
                    eConsumers.remove(i);
                }
                Utils.printToLog(ThreadSupervisor.class, null, " quedan " + eConsumers.size(), false, null, "support-level-1", Config.LOGGER_ERROR);
            }
            if(pushConsumers > pConsumers.size()){
                int toStart = eventConsumers - eConsumers.size();
                for(int i = 0; isAlive() && i <  toStart; ++i){
                    HecticusThread push = new PushManager(getRun());
                    Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(1, SECONDS), push, system.dispatcher());
                    push.setCancellable(cancellable);
                    pConsumers.add(push);
                }
            } else if(pushConsumers < pConsumers.size()){
                int toStop = pConsumers.size() - eventConsumers;
                for(int i = 0; isAlive() && i <  toStop; ++i){
                    HecticusThread push = pConsumers.get(i);
                    if(push.isActive()){
                        push.getCancellable().cancel();
                    }
                    pConsumers.remove(i);
                }
            }
        }
    }
}
