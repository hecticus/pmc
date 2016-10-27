package backend.caches;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import models.Config;
import models.apps.Application;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by plesse on 7/14/14.
 */
public class ClientsCache {

    private static ClientsCache me;
    private static long CACHE_TIMEOUT;
    private LoadingCache<String, Client> cache;

    public ClientsCache() {
        CACHE_TIMEOUT = Config.getLong("guava-caches-update-delay");
        cache = CacheBuilder.newBuilder().refreshAfterWrite(CACHE_TIMEOUT, TimeUnit.MINUTES).build(
                new CacheLoader<String, Client>(){
                    @Override
                    public Client load(String k) throws Exception {
                        return getClientFromWS(k);
                    }
                });
    }

    public static ClientsCache getInstance() {
        if (me == null) {
            me = new ClientsCache();
        }
        return me;
    }

    public Client getClient(String k) throws MalformedURLException, HTTPException, IOException, Exception {
        return cache.get(k);
    }

    private Client getClientFromWS(String k) {
        Client cl = null;
        String[] keyParts = k.split("-");
        Application app = Application.finder.byId(Long.parseLong(keyParts[0]));
        Promise<WSResponse> result = WS.url(app.getSingleClientUrl()+"/"+keyParts[1]).get();
        ObjectNode response = (ObjectNode)result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
        if((response != null) && (!Utils.checkIfResponseIsError(response))){
            cl = new Client((ObjectNode)response.get("response"));
        }
        return cl;
    }

    public void loadClients(backend.job.CacheLoader invoker, Application app, int batchSize) {
        int index = 0;
        boolean done = false;
        LinkedHashMap<String, Client> clients = new LinkedHashMap<String, Client>();
        Promise<WSResponse> result = null;
        WSResponse wsResponse = null;
        ObjectNode response = null;
        Iterator<JsonNode> clientsIterator = null;
        ObjectNode actualClient = null;
        long idClient = 0;
        while (invoker.isAlive() && !done) {
            try {
                result = WS.url(app.getBatchClientsUrl() + "/" + idClient + "/" + batchSize).get();
                wsResponse = result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS);
                if (wsResponse.getStatus() == 200) {
                    response = (ObjectNode) wsResponse.asJson();
                    if ((response != null) && (!Utils.checkIfResponseIsError(response))) {
                        done = true;
                        clientsIterator = response.get("response").elements();
                        while (invoker.isAlive() && clientsIterator.hasNext()) {
                            done = false;
                            actualClient = (ObjectNode) clientsIterator.next();
                            idClient = actualClient.get("idClient").asLong();
                            clients.put(generateClientKey(app.getIdApp(), actualClient), new Client(actualClient));
                        }
                    }
                } else {
                    Utils.printToLog(ClientsCache.class, null, "Error cargando clientes a la cache, app: " + app.getIdApp() + " llamada: " + app.getBatchClientsUrl() + "/" + index + "/" + batchSize, false, null, "support-level-1", Config.LOGGER_ERROR);
                    break;
                }
            } catch(Exception e) {
                Utils.printToLog(ClientsCache.class, null, "Error cargando clientes a la cache, app: " + app.getIdApp() + " llamada: " + app.getBatchClientsUrl() + "/" + index + "/" + batchSize, false, e, "support-level-1", Config.LOGGER_ERROR);
                if(e instanceof ConnectException) {
                    break;
                }
            }
            index+=batchSize;
        }
        if (!clients.isEmpty()){
            cache.asMap().putAll(clients);
        }
    }

    private String generateClientKey(Long idApp, ObjectNode actualClient) {
        return idApp + "-" + actualClient.get("idClient").asLong();
    }

}
