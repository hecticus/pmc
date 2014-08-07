package models.basic;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    @Constraints.Required
    private String server;

    public static Model.Finder<Long, Event> finder = new Model.Finder<Long, Event>(Long.class, Event.class);

    public Event(String type, String event, int retry, String server) {
        this.type = type;
        this.event = event;
        this.retry = retry;
        this.server = server;
    }

    public Event(String type, String event, String server) {
        this.type = type;
        this.event = event;
        this.retry = 0;
        this.server = server;
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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("idEvent", idEvent);
        response.put("type", type);
        response.put("event", event);
        response.put("retry", retry);
        response.put("server", server);
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
