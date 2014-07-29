package backend.caches;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by plesse on 7/14/14.
 */
public class Client {
    private long idClient;
    private long app;
    private ArrayList<String> droid;
    private ArrayList<String> ios;
    private ArrayList<String> web;
    private String msisdn;

    public Client(long idClient, long app, ArrayList<String> droid, ArrayList<String> ios, ArrayList<String> web, String msisdn) {
        this.idClient = idClient;
        this.app = app;
        this.droid = droid;
        this.ios = ios;
        this.web = web;
        this.msisdn = msisdn;
    }

    public Client(ObjectNode client) {
        this.idClient = client.get("idClient").asLong();
        this.app = client.get("app").asLong();
        if(client.has("droid")){
            Iterator<JsonNode> droidIDs = client.get("droid").elements();
            droid = new ArrayList<>();
            while(droidIDs.hasNext()) {
                droid.add(droidIDs.next().asText());
            }
        } else {
            this.droid = null;
        }

        if(client.has("ios")){
            Iterator<JsonNode> iosIDs = client.get("ios").elements();
            ios = new ArrayList<>();
            while(iosIDs.hasNext()) {
                ios.add(iosIDs.next().asText());
            }
        } else {
            this.ios = null;
        }

        if(client.has("web")){
            Iterator<JsonNode> webIDs = client.get("web").elements();
            web = new ArrayList<>();
            while(webIDs.hasNext()) {
                web.add(webIDs.next().asText());
            }
        } else {
            this.web = null;
        }

        if(client.has("msisdn")){
            this.msisdn = client.get("msisdn").asText();
        } else {
            this.msisdn = null;
        }

    }

    public long getIdClient() {
        return idClient;
    }

    public long getApp() {
        return app;
    }

    public ArrayList<String> getDroid() {
        return droid;
    }

    public ArrayList<String> getIos() {
        return ios;
    }

    public ArrayList<String> getWeb() {
        return web;
    }

    public String getMsisdn() {
        return msisdn;
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", app=" + app +
                ", droid=" + droid +
                ", ios=" + ios +
                ", web=" + web +
                ", msisdn='" + msisdn + '\'' +
                '}';
    }
}

