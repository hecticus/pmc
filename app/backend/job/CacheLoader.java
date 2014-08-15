package backend.job;

import akka.actor.Cancellable;
import backend.caches.Client;
import backend.caches.ClientsCache;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.basic.Config;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread para cargar la cache de clientes
 * Created by plesse on 7/18/14.
 */
public class CacheLoader extends HecticusThread {

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
    public void process() {
        long start = System.currentTimeMillis();
        try{
            int pageSize = Config.getInt("core-query-limit");
            List<Application> apps = Application.finder.all();
            for(int i = 0; isAlive() && i < apps.size(); ++i){
                if(apps.get(i).getActive() == 1 && apps.get(i).getBatchClientsUrl() != null && !apps.get(i).getBatchClientsUrl().isEmpty()) {
                    ClientsCache.getInstance().loadClients(this, apps.get(i), pageSize);
                }
            }
        } catch(Exception e) {
            Utils.printToLog(CacheLoader.class, "Error en el CacheLoader", "Ocurrio un error en el CacheLoader ", true, e, "support-level-1", Config.LOGGER_ERROR);
        } finally {
            Utils.printToLog(CacheLoader.class, null, "cache cargada en " + (System.currentTimeMillis() - start), false, null, "support-level-1", Config.LOGGER_INFO);
        }
    }

    /**
     * Metodo para obtener todos los idClients de la app recibida por parametro e ir llenando la cache de estos clientes
     * @param app       id del app
     * @param pageSize  maximo tamaño de pagina que se solicitar al ws
     * @deprecated
     */
    private void getClientsFromApp(Application app, int pageSize) {
        try{
            if(app.getActive() == 1 && app.getBatchClientsUrl() != null && !app.getBatchClientsUrl().isEmpty()){
                boolean done = false;
                int index = 0;
                while (isAlive() && !done) {
                    F.Promise<WSResponse> result = WS.url(app.getBatchClientsUrl() + "/" + index + "/" + pageSize).get();
                    ObjectNode response = (ObjectNode)result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
                    if (response != null && !Utils.checkIfResponseIsError(response)) {
                        Iterator<JsonNode> clients = response.get("response").elements();
                        int cant = 0;
                        while(isAlive() && clients.hasNext()) {
                            cant++;
                            JsonNode cl = clients.next();
                            try {
                                ClientsCache.getInstance().getClient(app.getIdApp() + "-" + cl.asLong());
                            } catch (Exception e) {
                                Utils.printToLog(CacheLoader.class, "", "No existe el cliente " + app.getIdApp() + "-" + cl.asLong(), false, null, "support-level-1", Config.LOGGER_ERROR);
                            }
                        }
                        done = cant != pageSize;
                    }
                    index += pageSize;
                }
            }
        } catch (Exception e){
            Utils.printToLog(CacheLoader.class, "Error en el CacheLoader", "Ocurrio un error en el CacheLoader ", false, e, "support-level-1", Config.LOGGER_ERROR);
        }
    }
}
