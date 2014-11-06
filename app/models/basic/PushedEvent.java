package models.basic;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import models.apps.Application;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by plesse on 11/5/14.
 */
@Entity
@Table(name="pushed_events")
public class PushedEvent extends HecticusModel {

    @Id
    private Long idPushedEvent;
    @ManyToOne
    @JoinColumn(name = "id_app")
    private Application application;
    @Constraints.Required
    private String message;
    @Constraints.Required
    private Long time;
    @Constraints.Required
    private Integer quantity;

    public static Model.Finder<Long, PushedEvent> finder = new Model.Finder<Long, PushedEvent>(Long.class, PushedEvent.class);

    public PushedEvent(Application application, String message, Long time, Integer quantity) {
        this.application = application;
        this.message = message;
        this.time = time;
        this.quantity = quantity;
    }

    public Long getIdPushedEvent() {
        return idPushedEvent;
    }

    public void setIdPushedEvent(Long idPushedEvent) {
        this.idPushedEvent = idPushedEvent;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getFormatedTime() {
        Date formatedDate = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        return sf.format(formatedDate);
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("idPushedEvent", idPushedEvent);
        response.put("application", application.toJson());
        response.put("message", message);
        response.put("time", time);
        response.put("quantity", quantity);
        return response;
    }

    public static Page<PushedEvent> page(int page, int pageSize, String sortBy, String order, long filter) {
        return finder.where().eq("application.idApp", filter).orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
}
