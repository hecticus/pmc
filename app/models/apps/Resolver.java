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
 * Created by plesse on 12/1/14.
 */
@Entity
@Table(name="resolvers")
public class Resolver extends HecticusModel {
    @Id
    private Long idResolver;
    @Constraints.Required
    private String className;
    @Constraints.Required
    private String name;

    @OneToOne
    @JoinColumn(name = "id_device")
    private Device device;

    public static Model.Finder<Long, Resolver> finder = new Model.Finder<Long, Resolver>(Long.class, Resolver.class);

    public Resolver(String className) {
        this.className = className;
    }

    public Long getIdResolver() {
        return idResolver;
    }

    public void setIdResolver(Long idResolver) {
        this.idResolver = idResolver;
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
        response.put("id_resolver", idResolver);
        response.put("class_name", className);
        response.put("name", name);
        response.put("device", device.toJson());
        return response;
    }

    public static scala.collection.immutable.List<Tuple2<String, String>> toSeq() {
        List<Resolver> resolvers = Resolver.finder.all();
        ArrayList<Tuple2<String, String>> proxy = new ArrayList<>();
        for(Resolver resolver : resolvers) {
            Tuple2<String, String> t = new Tuple2<>(resolver.getIdResolver().toString(), resolver.getName());
            proxy.add(t);
        }
        Buffer<Tuple2<String, String>> resolverBuffer = JavaConversions.asScalaBuffer(proxy);
        scala.collection.immutable.List<Tuple2<String, String>> resolverList = resolverBuffer.toList();
        return resolverList;
    }

    public static scala.collection.immutable.List<Tuple2<String, String>> toSeqP(int deviceId) {
        List<Resolver> resolvers = Resolver.finder.where().eq("device.idDevice", deviceId).findList();
        ArrayList<Tuple2<String, String>> proxy = new ArrayList<>();
        for(Resolver resolver : resolvers) {
            Tuple2<String, String> t = new Tuple2<>(resolver.getIdResolver().toString(), resolver.getName());
            proxy.add(t);
        }
        Buffer<Tuple2<String, String>> resolverBuffer = JavaConversions.asScalaBuffer(proxy);
        scala.collection.immutable.List<Tuple2<String, String>> resolverList = resolverBuffer.toList();
        return resolverList;
    }

    public static Page<Resolver> page(int page, int pageSize, String sortBy, String order, String filter) {
        return finder.where().ilike("name", "%" + filter + "%").orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }

}
