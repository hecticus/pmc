package models.apps;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;

/**
 * Created by plesse on 7/10/14.
 */
@Entity
@Table(name="app_device")
public class AppDevice  extends HecticusModel {
    @Id
    private Long idAppDevice;
    @Constraints.Required
    private int status;
    @ManyToOne
    @JoinColumn(name = "id_app")
    Application app;
    @ManyToOne
    @JoinColumn(name = "id_device")
    Device dev;


    public static Model.Finder<Long, AppDevice> finder = new Model.Finder<Long, AppDevice>(Long.class, AppDevice.class);

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("idAppDevice", idAppDevice);
        response.put("status", status);
        response.put("app", app.toJson());
        response.put("dev", dev.toJson());
        return response;
    }

    public int getStatus() {
        return status;
    }

    public Application getApp() {
        return app;
    }

    public Device getDev() {
        return dev;
    }

    public Long getIdAppDevice() {
        return idAppDevice;
    }

    public void setIdAppDevice(Long idAppDevice) {
        this.idAppDevice = idAppDevice;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDev(Device dev) {
        this.dev = dev;
    }

    public void setApp(Application app) {
        this.app = app;
    }
}