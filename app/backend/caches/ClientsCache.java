package backend.caches;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import models.apps.Application;
import models.basic.Config;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.Utils;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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

    public void loadClients(Application app, int batchSize) {
        int index = 0;
        boolean done = false;
        LinkedHashMap<String, Client> clients = new LinkedHashMap<String, Client>();
        while (!done) {
            try {
                Promise<WSResponse> result = WS.url(app.getBatchClientsUrl() + "/" + index + "/" + batchSize).get();
                ObjectNode response = (ObjectNode) result.get(Config.getLong("ws-timeout-millis"), TimeUnit.MILLISECONDS).asJson();
                if ((response != null) && (!Utils.checkIfResponseIsError(response))) {
                    done = true;
                    Iterator<JsonNode> clientsIterator = response.get("response").elements();
                    while(clientsIterator.hasNext()){
                        done = false;
                        ObjectNode actualClient = (ObjectNode) clientsIterator.next();
                        clients.put(generateClientKey(app.getIdApp(), actualClient), new Client(actualClient));
                    }
                }
            } catch(Exception e) {
                Utils.printToLog(ClientsCache.class, null, "Error cargando clientes a la cache, app: " + app.getIdApp() + " llamada: " + app.getBatchClientsUrl() + "/" + index + "/" + batchSize, false, e, "support-level-1", Config.LOGGER_ERROR);
            }
            index++;
        }
        if (!clients.isEmpty()){
            cache.asMap().putAll(clients);
        }
    }

    private String generateClientKey(Long idApp, ObjectNode actualClient) {
        return idApp + "-" + actualClient.get("idClient").asLong();
    }

}
