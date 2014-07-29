package models.apps;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by plesse on 7/10/14.
 */
@Entity
@Table(name="app")
public class Application extends HecticusModel {
    @Id
    private Long idApp;
    @Constraints.Required
    private String name;
    @Constraints.Required
    private int active;
    @OneToMany(mappedBy="app")
    private List<AppDevice> appDevices;

    @Constraints.Required
    private String batchClientsUrl;

    @Constraints.Required
    private String singleClientUrl;

    @Constraints.Required
    private String cleanDeviceUrl;

    @Constraints.Required
    private String iosPushApnsCertProduction;

    @Constraints.Required
    private String iosPushApnsCertSandbox;

    @Constraints.Required
    private String iosPushApnsPassphrase;

    @Constraints.Required
    private String googleApiKey;

    @Constraints.Required
    private String mailgunApikey;

    @Constraints.Required
    private String mailgunFrom;

    @Constraints.Required
    private String mailgunTo;

    @Constraints.Required
    private String mailgunApiurl;

    @Constraints.Required
    private String title;

    @Constraints.Required
    private String sound;

    @Constraints.Required
    private int debug;

    @Constraints.Required
    private int iosSandbox;


    public static Model.Finder<Long, Application> finder = new Model.Finder<Long, Application>(Long.class, Application.class);

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("idApp", idApp);
        response.put("name", name);
        response.put("active", active);
        response.put("batchClientsUrl", batchClientsUrl);
        response.put("singleClientUrl", singleClientUrl);
        response.put("cleanDeviceUrl", cleanDeviceUrl);
        response.put("iosPushApnsCertProduction", iosPushApnsCertProduction);
        response.put("iosPushApnsCertProduction", iosPushApnsCertSandbox);
        response.put("iosPushApnsPassphrase", iosPushApnsPassphrase);
        response.put("googleApiKey", googleApiKey);
        response.put("mailgunApikey", mailgunApikey);
        response.put("mailgunFrom", mailgunFrom);
        response.put("mailgunTo", mailgunTo);
        response.put("mailgunApiurl", mailgunApiurl);
        response.put("title", title);
        response.put("sound", sound);
        response.put("debug", debug);
        if(appDevices != null && !appDevices.isEmpty()){
            ArrayList<ObjectNode> apps = new ArrayList<>();
            for(AppDevice ad : appDevices){
                apps.add(ad.getDev().toJsonWithoutApps());
            }
            response.put("devs", Json.toJson(apps));
        }
        return response;
    }

    public ObjectNode toJsonWithoutDevices() {
        ObjectNode response = Json.newObject();
        response.put("idApp", idApp);
        response.put("name", name);
        response.put("active", active);
        response.put("batchClientsUrl", batchClientsUrl);
        response.put("singleClientUrl", singleClientUrl);
        response.put("cleanDeviceUrl", cleanDeviceUrl);
        response.put("iosPushApnsCertProduction", iosPushApnsCertProduction);
        response.put("iosPushApnsCertProduction", iosPushApnsCertSandbox);
        response.put("iosPushApnsPassphrase", iosPushApnsPassphrase);
        response.put("googleApiKey", googleApiKey);
        response.put("mailgunApikey", mailgunApikey);
        response.put("mailgunFrom", mailgunFrom);
        response.put("mailgunTo", mailgunTo);
        response.put("mailgunApiurl", mailgunApiurl);
        response.put("title", title);
        response.put("sound", sound);
        response.put("debug", debug);
        return response;
    }

    public Long getIdApp() {
        return idApp;
    }

    public String getName() {
        return name;
    }

    public int getActive() {
        return active;
    }

    public String getBatchClientsUrl() {
        return batchClientsUrl;
    }

    public String getSingleClientUrl() {
        return singleClientUrl;
    }

    public List<AppDevice> getAppDevices() {
        return appDevices;
    }

    public String getIosPushApnsCertProduction() {
        return iosPushApnsCertProduction;
    }

    public String getIosPushApnsPassphrase() {
        return iosPushApnsPassphrase;
    }

    public String getGoogleApiKey() {
        return googleApiKey;
    }

    public String getMailgunApikey() {
        return mailgunApikey;
    }

    public String getMailgunFrom() {
        return mailgunFrom;
    }

    public String getMailgunTo() {
        return mailgunTo;
    }

    public String getMailgunApiurl() {
        return mailgunApiurl;
    }

    public String getTitle() {
        return title;
    }

    public String getSound() {
        return sound;
    }

    public int getDebug() {
        return debug;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void setBatchClientsUrl(String batchClientsUrl) {
        this.batchClientsUrl = batchClientsUrl;
    }

    public void setSingleClientUrl(String singleClientUrl) {
        this.singleClientUrl = singleClientUrl;
    }

    public void setIosPushApnsCertProduction(String iosPushApnsCertProduction) {
        this.iosPushApnsCertProduction = iosPushApnsCertProduction;
    }

    public void setIosPushApnsPassphrase(String iosPushApnsPassphrase) {
        this.iosPushApnsPassphrase = iosPushApnsPassphrase;
    }

    public void setGoogleApiKey(String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    public void setMailgunApikey(String mailgunApikey) {
        this.mailgunApikey = mailgunApikey;
    }

    public void setMailgunFrom(String mailgunFrom) {
        this.mailgunFrom = mailgunFrom;
    }

    public void setMailgunTo(String mailgunTo) {
        this.mailgunTo = mailgunTo;
    }

    public void setMailgunApiurl(String mailgunApiurl) {
        this.mailgunApiurl = mailgunApiurl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    public String getIosPushApnsCertSandbox() {
        return iosPushApnsCertSandbox;
    }

    public void setIosPushApnsCertSandbox(String iosPushApnsCertSandbox) {
        this.iosPushApnsCertSandbox = iosPushApnsCertSandbox;
    }

    public int getIosSandbox() {
        return iosSandbox;
    }

    public void setIosSandbox(int iosSandbox) {
        this.iosSandbox = iosSandbox;
    }

    public String getCleanDeviceUrl() {
        return cleanDeviceUrl;
    }

    public void setCleanDeviceUrl(String cleanDeviceUrl) {
        this.cleanDeviceUrl = cleanDeviceUrl;
    }
}
