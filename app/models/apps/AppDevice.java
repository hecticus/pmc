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
    private Boolean status;
    @ManyToOne
    @JoinColumn(name = "id_app")
    Application app;
    @ManyToOne
    @JoinColumn(name = "id_device")
    private Device dev;
    @OneToOne
    @JoinColumn(name = "id_pusher")
    private Pusher pusher;
    @OneToOne
    @JoinColumn(name = "id_resolver")
    private Resolver resolver;
    @OneToOne
    @JoinColumn(name = "id_cleaner")
    private Cleaner cleaner;

    @Transient
    private boolean send;

    @Transient
    private StringBuilder ids;

    public static Model.Finder<Long, AppDevice> finder = new Model.Finder<>(Long.class, AppDevice.class);

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("idAppDevice", idAppDevice);
        response.put("status", status);
        response.put("app", app.toJson());
        response.put("dev", dev.toJson());
        response.put("pusher", pusher.toJson());
        response.put("resolver", resolver.toJson());
        response.put("cleaner", cleaner.toJson());
        return response;
    }

    public boolean getStatus() {
        if(status == null) status = false;
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

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setDev(Device dev) {
        this.dev = dev;
    }

    public void setApp(Application app) {
        this.app = app;
    }

    public Pusher getPusher() {
        return pusher;
    }

    public void setPusher(Pusher pusher) {
        this.pusher = pusher;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Resolver getResolver() {
        return resolver;
    }

    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    public Cleaner getCleaner() {
        return cleaner;
    }

    public void setCleaner(Cleaner cleaner) {
        this.cleaner = cleaner;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public StringBuilder getIds() {
        return ids;
    }

    public void setIds(StringBuilder ids) {
        this.ids = ids;
    }

    public StringBuilder appendId(String id){
        if(ids == null) ids = new StringBuilder();
        ids.append(id).append(",");
        return ids;
    }

    public void clearIds(){
        if(ids != null){
            ids.delete(0, ids.length());
        }
    }
}