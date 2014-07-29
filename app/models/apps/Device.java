package models.apps;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by plesse on 7/10/14.
 */
@Entity
@Table(name="device")
public class Device extends HecticusModel {

    @Id
    private Long idDevice;
    @Constraints.Required
    private String name;


    @OneToMany(mappedBy="dev")
    private List<AppDevice> appDevices;

    public static Model.Finder<Long, Device> finder = new Model.Finder<Long, Device>(Long.class, Device.class);

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("idDevice", idDevice);
        response.put("name", name);
        if(appDevices != null && !appDevices.isEmpty()){
            ArrayList<ObjectNode> apps = new ArrayList<>();
            for(AppDevice ad : appDevices){
                apps.add(ad.getApp().toJsonWithoutDevices());
            }
            response.put("apps", Json.toJson(apps));
        }
        return response;
    }

    public ObjectNode toJsonWithoutApps() {
        ObjectNode response = Json.newObject();
        response.put("idDevice", idDevice);
        response.put("name", name);
        return response;
    }

    public String getName() {
        return name;
    }
}
