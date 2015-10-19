package models.apps;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import org.apache.commons.lang3.StringEscapeUtils;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.mutable.Buffer;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by plessmann on 21/07/15.
 */
@Entity
@Table(name="pushers")
public class Pusher extends HecticusModel {
    @Id
    private Long idPusher;
    @Constraints.Required
    private String className;

    @Constraints.Required
    private String name;

    private String params;

    @OneToOne
    @JoinColumn(name = "id_device")
    private Device device;

    public Pusher(String className, String params) {
        this.className = className;
        this.params = params;
    }

    public static Model.Finder<Long, Pusher> finder = new Model.Finder<>(Long.class, Pusher.class);

    public Pusher(String className) {
        this.className = className;
    }

    public Long getIdPusher() {
        return idPusher;
    }

    public void setIdPusher(Long idPusher) {
        this.idPusher = idPusher;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
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

    public Map getParsedParams() throws IOException {
        if (params != null && !params.isEmpty()) {
            String tempParams = StringEscapeUtils.unescapeHtml4(params);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(tempParams, LinkedHashMap.class);
        }
        return null;
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("id_pusher", idPusher);
        response.put("class_name", className);
        response.put("name", name);
        response.put("device", device.toJson());
        return response;
    }

    public static scala.collection.immutable.List<Tuple2<String, String>> toSeq() {
        List<Pusher> pushers = finder.all();
        ArrayList<Tuple2<String, String>> proxy = new ArrayList<>();
        for(Pusher pusher : pushers) {
            Tuple2<String, String> t = new Tuple2<>(pusher.getIdPusher().toString(), pusher.getName());
            proxy.add(t);
        }
        Buffer<Tuple2<String, String>> pusherBuffer = JavaConversions.asScalaBuffer(proxy);
        scala.collection.immutable.List<Tuple2<String, String>> pusherList = pusherBuffer.toList();
        return pusherList;
    }

    public static scala.collection.immutable.List<Tuple2<String, String>> toSeqP(int deviceId) {
        List<Pusher> pushers = finder.where().eq("device.idDevice", deviceId).findList();
        ArrayList<Tuple2<String, String>> proxy = new ArrayList<>();
        for(Pusher pusher : pushers) {
            Tuple2<String, String> t = new Tuple2<>(pusher.getIdPusher().toString(), pusher.getName());
            proxy.add(t);
        }
        Buffer<Tuple2<String, String>> pusherBuffer = JavaConversions.asScalaBuffer(proxy);
        scala.collection.immutable.List<Tuple2<String, String>> pusherList = pusherBuffer.toList();
        return pusherList;
    }

    public static Page<Pusher> page(int page, int pageSize, String sortBy, String order, String filter) {
        return finder.where().ilike("name", "%" + filter + "%").orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }

}
