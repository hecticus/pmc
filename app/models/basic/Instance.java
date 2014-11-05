package models.basic;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by plesse on 8/8/14.
 */
@Entity
@Table(name="instances")
public class Instance extends HecticusModel {

    @Id
    private Long idInstance;
    @Constraints.Required
    private String ip;
    @Constraints.Required
    private String name;
    @Constraints.Required
    private int running;
    @Constraints.Required
    private int test;

    public static Model.Finder<Long, Instance> finder = new Model.Finder<Long, Instance>(Long.class, Instance.class);

    public Instance(String ip, String name, int running) {
        this.ip = ip;
        this.name = name;
        this.running = running;
    }

    public Instance(String ip, String name, int running, int test) {
        this.ip = ip;
        this.name = name;
        this.running = running;
        this.test = test;
    }

    public Long getIdInstance() {
        return idInstance;
    }

    public void setIdInstance(Long idInstance) {
        this.idInstance = idInstance;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("idInstance", idInstance);
        response.put("ip", ip);
        response.put("name", name);
        response.put("running", running);
        response.put("test", test);
        return response;
    }

    public static void save(Instance i){
        EbeanServer server = Ebean.getServer("default");
        server.save(i);
    }

    public static void delete(Instance i){
        EbeanServer server = Ebean.getServer("default");
        server.delete(i);
    }

    public static void update(Instance i){
        EbeanServer server = Ebean.getServer("default");
        server.update(i);
    }

    public static Page<Instance> page(int page, int pageSize, String sortBy, String order, String filter) {
        return finder.where().orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
}
