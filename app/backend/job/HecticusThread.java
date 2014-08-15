package backend.job;

import akka.actor.Cancellable;
import models.basic.Config;
import utils.Utils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase base de los hilos del PMC
 *
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

    /**
     * Metodo que ejecuta la funcionalidad real de un HecticusThread
     */
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

    /**
     * Funcion para que un hilo sepa si aun el PMC esta vivo
     *
     * @return      true si el PMC esta vivo
     */
    public boolean isAlive(){
        prevTime = actTime;
        actTime = System.currentTimeMillis();
        boolean canExecute = run.get();
        if(cancellable != null) {
            canExecute &= !cancellable.isCancelled();
        }
        return canExecute;
    }

    /**
     * Funcion para obtener el tiempo que ha pasado el hilo  corriendo
     *
     * @return      tiempo entre pasos por el marcador de tiempo
     */
    public long runningTime(){
        return actTime - prevTime;
    }

    /**
     * Metodo de ejecucion de los HecticusThread
     */
    @Override
    public void run() {
        if(isAlive() && !active){
            try{
                active = true;
                process();
            } catch (Throwable t){
                Utils.printToLog(HecticusThread.class, "Error en el HecticusThread", "Ocurrio un error que llego hasta el HecticusThread: " + name, true, t, "support-level-1", Config.LOGGER_ERROR);
            } finally {
                setAlive();
                active = false;
            }
        }
    }

    public void stop() {
        //doSomething...
    }

    public void cancel() {
        try{
            active = false;
            stop();
            if(cancellable != null) {
                cancellable.cancel();
            }
            Utils.printToLog(HecticusThread.class, null, "Apagado " + name + " vivio " + (System.currentTimeMillis() - initTime), false, null, "support-level-1", Config.LOGGER_INFO);
        } catch (Throwable t){
            Utils.printToLog(HecticusThread.class, "Error en el HecticusThread", "Ocurrio cancelando el HecticusThread: " + name, true, t, "support-level-1", Config.LOGGER_ERROR);
        }
    }

}
