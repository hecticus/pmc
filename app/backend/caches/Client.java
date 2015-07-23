package backend.caches;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.AppDevice;
import models.apps.Device;

import java.util.*;

/**
 * Created by plesse on 7/14/14.
 */
public class Client {
    private long idClient;
    private long app;
    private Map<Long, ArrayList<String>> deviceRelation;

    public Client(ObjectNode client) {
        deviceRelation = new HashMap<>();
        this.idClient = client.get("idClient").asLong();
        this.app = client.get("app").asLong();
        Iterator<JsonNode> ids;
        ArrayList<String> finalIds = new ArrayList<>();
        List<Device> devices = Device.finder.all();
        for(Device device : devices){
            if(client.has(device.getName())){
                ids = client.get(device.getName()).elements();
                while(ids.hasNext()) {
                    finalIds.add(ids.next().asText());
                }
                deviceRelation.put(device.getIdDevice(), (ArrayList<String>) finalIds.clone());
                finalIds.clear();
            }
        }
    }

    public long getIdClient() {
        return idClient;
    }

    public long getApp() {
        return app;
    }

    public void sortRegIDs(ArrayList<AppDevice> allowedDevices) {
        for (AppDevice allowedDevice : allowedDevices){
            ArrayList<String> regIDs = deviceRelation.get(allowedDevice.getDev().getIdDevice());
            if(regIDs != null && !regIDs.isEmpty()){
                for (String regId : regIDs){
                    allowedDevice.appendId(regId);
                }
            }
        }
    }
}

