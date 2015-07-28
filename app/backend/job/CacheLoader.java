package backend.job;

import akka.actor.Cancellable;
import backend.HecticusThread;
import backend.caches.ClientsCache;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.Config;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread para cargar la cache de clientes
 * Created by plesse on 7/18/14.
 */
public class CacheLoader extends HecticusThread {

    public CacheLoader() {
        this.setActTime(System.currentTimeMillis());
        this.setInitTime(System.currentTimeMillis());
        this.setPrevTime(System.currentTimeMillis());
        //set name
        this.setName("CacheLoader-" + System.currentTimeMillis());
    }

    public CacheLoader(String name, AtomicBoolean run, Cancellable cancellable) {
        super("CacheLoader-"+name, run, cancellable);
    }

    public CacheLoader(String name, AtomicBoolean run) {
        super("CacheLoader-"+name, run);
    }

    public CacheLoader(AtomicBoolean run) {
        super("CacheLoader",run);
    }

    /**
     * Metodo para ir llenando la cache a partir de las apps registradas en el PMC
     */
    @Override
    public void process(Map args) {
        long start = System.currentTimeMillis();
        try{
            int pageSize = (int) args.get("page-limit");
            List<Application> apps = Application.finder.all();
            for(int i = 0; isAlive() && i < apps.size(); ++i){
                if(apps.get(i).isActive() && apps.get(i).getBatchClientsUrl() != null && !apps.get(i).getBatchClientsUrl().isEmpty()) {
                    ClientsCache.getInstance().loadClients(this, apps.get(i), pageSize);
                }
            }
        } catch(Exception e) {
            Utils.printToLog(CacheLoader.class, "Error en el CacheLoader", "Ocurrio un error en el CacheLoader ", true, e, "support-level-1", Config.LOGGER_ERROR);
        } finally {
            Utils.printToLog(CacheLoader.class, null, "cache cargada en " + (System.currentTimeMillis() - start), false, null, "support-level-1", Config.LOGGER_INFO);
        }
    }

}
