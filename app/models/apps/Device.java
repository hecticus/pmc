package models.apps;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.mutable.Buffer;

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

    public Long getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(Long idDevice) {
        this.idDevice = idDevice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static scala.collection.immutable.List<Tuple2<String, String>> toSeq() {
        List<Device> devices = Device.finder.all();
        ArrayList<Tuple2<String, String>> proxy = new ArrayList<>();
        for(Device device : devices) {
            Tuple2<String, String> t = new Tuple2<>(device.getIdDevice().toString(), device.getName());
            proxy.add(t);
        }
        Buffer<Tuple2<String, String>> deviceBuffer = JavaConversions.asScalaBuffer(proxy);
        scala.collection.immutable.List<Tuple2<String, String>> deviceList = deviceBuffer.toList();
        return deviceList;
    }

    public static Page<Device> page(int page, int pageSize, String sortBy, String order, String filter) {
        return finder.where().ilike("name", "%" + filter + "%").orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }

}
