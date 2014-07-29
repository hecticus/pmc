package backend.caches;

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
}
