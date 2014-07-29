package backend.job;

import akka.actor.Cancellable;
import models.basic.Config;
import utils.Utils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by plesse on 7/9/14.
 */
public abstract class HecticusThread implements Runnable {
    private String name;
    private long initTime;
    private AtomicBoolean run;
    private long prevTime;
    private long actTime;
    private boolean active;
    private Cancellable cancellable;

    protected HecticusThread(String name,  AtomicBoolean run, Cancellable cancellable) {
        this.initTime = this.actTime = this.prevTime = System.currentTimeMillis();
        this.name = name+"-"+initTime;
        this.run = run;
        this.cancellable = cancellable;
        this.active = false;
    }

    protected HecticusThread(String name,  AtomicBoolean run) {
        this.initTime = this.actTime = this.prevTime = System.currentTimeMillis();
        this.name = name+"-"+initTime;
        this.run = run;
        this.active = false;
    }

    protected HecticusThread(AtomicBoolean run) {
        this.initTime = this.actTime = this.prevTime = System.currentTimeMillis();
        this.name = "HecticusThread-"+initTime;
        this.run = run;
        this.active = false;
    }

    public abstract void process();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInitTime() {
        return initTime;
    }

    public AtomicBoolean getRun() {
        return run;
    }

    public boolean isActive() {
        return active;
    }

    public Cancellable getCancellable() {
        return cancellable;
    }

    public void setCancellable(Cancellable cancellable) {
        this.cancellable = cancellable;
    }

    public void setAlive(){
        prevTime = actTime;
        actTime = System.currentTimeMillis();
    }

    public boolean isAlive(){
        prevTime = actTime;
        actTime = System.currentTimeMillis();
        return run.get();
    }

    public long runningTime(){
        return actTime - prevTime;
    }

    @Override
    public void run() {
        if(run.get()){
            try{
                active = true;
                setAlive();
                process();
            } catch (Throwable t){
                Utils.printToLog(HecticusThread.class, "Error en el HecticusThread", "Ocurrio un error que llego hasta el HecticusThread: " + name, true, t, "support-level-1", Config.LOGGER_ERROR);
            } finally {
                setAlive();
                active = false;
            }
        }
    }



}
