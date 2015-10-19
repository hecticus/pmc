package models.apps;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import models.HecticusModel;
import models.basic.PushedEvent;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.i18n.Messages;
import play.libs.Json;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.mutable.Buffer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
    private Boolean active;
    @OneToMany(mappedBy="app", cascade = CascadeType.ALL)
    private List<AppDevice> appDevices;

    @OneToMany(mappedBy="application", cascade = CascadeType.ALL)
    private List<PushedEvent> pushedEvents;

    @Constraints.Required
    private String batchClientsUrl;

    @Constraints.Required
    private String singleClientUrl;

    @Constraints.Required
    private String cleanDeviceUrl;

    private String iosPushApnsCertProduction;

    private String iosPushApnsCertSandbox;

    private String iosPushApnsPassphrase;

    private String googleApiKey;

    private String mailgunApikey;

    private String mailgunFrom;

    private String mailgunTo;

    private String mailgunApiurl;

    @Constraints.Required
    private String title;

    private String sound;

    private Boolean debug;

    private Boolean iosSandbox;

    public String validate() {
        if(iosPushApnsCertProduction != null && !iosPushApnsCertProduction.isEmpty()){
            if(!iosPushApnsCertProduction.endsWith(".p12")){
                return Messages.get("applications.java.invalidIosProductionFile");
            }
        }
        if(iosPushApnsCertSandbox != null && !iosPushApnsCertSandbox.isEmpty()){
            if(!iosPushApnsCertSandbox.endsWith(".p12")){
                return Messages.get("applications.java.invalidIosSandboxFile");
            }
        }
        return null;
    }


    public static Model.Finder<Long, Application> finder = new Model.Finder<Long, Application>(Long.class, Application.class);

    public Application() {
        this.iosPushApnsCertProduction = "";
        this.iosPushApnsCertSandbox = "";
        this.iosPushApnsPassphrase = "";
        this.googleApiKey = "";
        this.mailgunApikey = "";
        this.mailgunFrom = "";
        this.mailgunTo = "";
        this.mailgunApiurl = "";
        this.sound = "";
        this.iosSandbox = false;
    }

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

    public boolean isActive() {
        if(active == null) active = false;
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

    public boolean isDebug() {
        if(debug == null) debug = false;
        return debug;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
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

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getIosPushApnsCertSandbox() {
        return iosPushApnsCertSandbox;
    }

    public void setIosPushApnsCertSandbox(String iosPushApnsCertSandbox) {
        this.iosPushApnsCertSandbox = iosPushApnsCertSandbox;
    }

    public boolean isIosSandbox() {
        return iosSandbox;
    }

    public void setIosSandbox(boolean iosSandbox) {
        this.iosSandbox = iosSandbox;
    }

    public String getCleanDeviceUrl() {
        return cleanDeviceUrl;
    }

    public void setCleanDeviceUrl(String cleanDeviceUrl) {
        this.cleanDeviceUrl = cleanDeviceUrl;
    }

    public void setIdApp(Long idApp) {
        this.idApp = idApp;
    }

    public void setAppDevices(List<AppDevice> appDevices) {
        this.appDevices = appDevices;
    }

    public List<PushedEvent> getPushedEvents() {
        return pushedEvents;
    }

    public void setPushedEvents(List<PushedEvent> pushedEvents) {
        this.pushedEvents = pushedEvents;
    }

    public static Page<Application> page(int page, int pageSize, String sortBy, String order, String filter) {
        return finder.where().ilike("name", "%" + filter + "%").orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }

    public AppDevice getDevice(final String type){
        AppDevice appDevice;
        try {
            appDevice = Iterables.find(appDevices, new Predicate<AppDevice>() {
                public boolean apply(AppDevice obj) {
                    return obj.getDev().getName().equalsIgnoreCase(type);
                }
            });
        } catch (NoSuchElementException ex){
            appDevice = null;
        }
        return appDevice;
    }

    public boolean isDeviceActive(final String type){
        AppDevice appDevice;
        try {
            appDevice = Iterables.find(appDevices, new Predicate<AppDevice>() {
                public boolean apply(AppDevice obj) {
                    return obj.getDev().getName().equalsIgnoreCase(type);
                }
            });
        } catch (NoSuchElementException ex){
            appDevice = null;
        }
        if(appDevice != null) {
            return appDevice.getStatus();
        }
        return false;
    }

    public static scala.collection.immutable.List<Tuple2<String, String>> toSeq() {
        List<Application> applications = Application.finder.all();
        ArrayList<Tuple2<String, String>> proxy = new ArrayList<>();
        for(Application application : applications) {
            Tuple2<String, String> t = new Tuple2<>(application.getIdApp().toString(), application.getName());
            proxy.add(t);
        }
        Buffer<Tuple2<String, String>> appBuffer = JavaConversions.asScalaBuffer(proxy);
        scala.collection.immutable.List<Tuple2<String, String>> appList = appBuffer.toList();
        return appList;
    }
}
