package models.basic;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import models.Instance;
import models.apps.Device;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;

/**
 * Created by plesse on 7/31/14.
 */
@Entity
@Table(name="event")
public class Event extends HecticusModel {

    @Id
    private Long idEvent;
    @Constraints.Required
    private String type;
    @Constraints.Required
    private String event;
    @Constraints.Required
    private int retry;
    @ManyToOne
    @JoinColumn(name = "id_instance")
    private Instance instance;

    public static Model.Finder<Long, Event> finder = new Model.Finder<>(Long.class, Event.class);

    public Event(String type, String event, int retry, Instance instance) {
        this.type = type;
        this.event = event;
        this.retry = retry;
        this.instance = instance;
    }

    public Event(String type, String event, Instance instance) {
        this.type = type;
        this.event = event;
        this.retry = 0;
        this.instance = instance;
    }

    public Long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Long idEvent) {
        this.idEvent = idEvent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("idEvent", idEvent);
        response.put("type", type);
        response.put("event", event);
        response.put("retry", retry);
        response.put("instance", instance.toJson());
        return response;
    }

    public static void save(Event e){
        EbeanServer server = Ebean.getServer("default");
        server.save(e);
    }

    public static void delete(Event e){
        EbeanServer server = Ebean.getServer("default");
        server.delete(e);
    }

    public static void update(Event e){
        EbeanServer server = Ebean.getServer("default");
        server.update(e);
    }

}
