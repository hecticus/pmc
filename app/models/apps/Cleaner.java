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
 * Created by plessmann on 23/07/15.
 */
@Entity
@Table(name="cleaners")
public class Cleaner extends HecticusModel {
    @Id
    private Long idCleaner;
    @Constraints.Required
    private String className;
    @Constraints.Required
    private String name;

    @OneToOne
    @JoinColumn(name = "id_device")
    private Device device;

    public static Model.Finder<Long, Cleaner> finder = new Model.Finder<>(Long.class, Cleaner.class);

    public Cleaner(String className) {
        this.className = className;
    }

    public Long getIdCleaner() {
        return idCleaner;
    }

    public void setIdCleaner(Long idCleaner) {
        this.idCleaner = idCleaner;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("id_cleaner", idCleaner);
        response.put("class_name", className);
        response.put("name", name);
        response.put("device", device.toJson());
        return response;
    }

    public static scala.collection.immutable.List<Tuple2<String, String>> toSeq() {
        List<Cleaner> cleaners = Cleaner.finder.all();
        ArrayList<Tuple2<String, String>> proxy = new ArrayList<>();
        for(Cleaner cleaner : cleaners) {
            Tuple2<String, String> t = new Tuple2<>(cleaner.getIdCleaner().toString(), cleaner.getName());
            proxy.add(t);
        }
        Buffer<Tuple2<String, String>> cleanerBuffer = JavaConversions.asScalaBuffer(proxy);
        scala.collection.immutable.List<Tuple2<String, String>> cleanerList = cleanerBuffer.toList();
        return cleanerList;
    }

    public static scala.collection.immutable.List<Tuple2<String, String>> toSeqP(int deviceId) {
        List<Cleaner> cleaners = Cleaner.finder.where().eq("device.idDevice", deviceId).findList();
        ArrayList<Tuple2<String, String>> proxy = new ArrayList<>();
        for(Cleaner cleaner : cleaners) {
            Tuple2<String, String> t = new Tuple2<>(cleaner.getIdCleaner().toString(), cleaner.getName());
            proxy.add(t);
        }
        Buffer<Tuple2<String, String>> cleanerBuffer = JavaConversions.asScalaBuffer(proxy);
        scala.collection.immutable.List<Tuple2<String, String>> cleanerList = cleanerBuffer.toList();
        return cleanerList;
    }

    public static Page<Cleaner> page(int page, int pageSize, String sortBy, String order, String filter) {
        return finder.where().ilike("name", "%" + filter + "%").orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
}
