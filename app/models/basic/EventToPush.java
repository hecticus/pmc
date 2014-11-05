package models.basic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import models.apps.Application;
import play.data.validation.Constraints;
import play.libs.Json;

import javax.persistence.Id;

/**
 * Created by plesse on 11/5/14.
 */
public class EventToPush extends HecticusModel {

    @Id
    private Long idEventToPush;
    @Constraints.Required
    private Application application;
    @Constraints.Required
    private String message;
    @Constraints.Required
    private String extraParams;
//    @Constraints.Required
    private String receivers;
//    @Constraints.Required
    private Integer type;

    private Boolean all;

    public EventToPush() {
    }

    public EventToPush(Application application, String message, String extraParams, String receivers) {
        this.application = application;
        this.message = message;
        this.extraParams = extraParams;
        this.receivers = receivers;
    }

    public Long getIdEventToPush() {
        return idEventToPush;
    }

    public void setIdEventToPush(Long idEventToPush) {
        this.idEventToPush = idEventToPush;
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

    public String getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getAll() {
        return all;
    }

    public void setAll(Boolean all) {
        this.all = all;
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("app", application.getIdApp());
        response.put("msg", message);
        response.put("extra_params", extraParams);
        response.put("regIDs", receivers);
        return response;
    }
}
