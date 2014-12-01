package models.apps;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.HecticusModel;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.mutable.Buffer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @Override
    public ObjectNode toJson() {
        ObjectNode response = Json.newObject();
        response.put("id_resolver", idResolver);
        response.put("class_name", className);
        return response;
    }

    public static scala.collection.immutable.List<Tuple2<String, String>> toSeq() {
        List<Resolver> resolvers = Resolver.finder.all();
        ArrayList<Tuple2<String, String>> proxy = new ArrayList<>();
        for(Resolver resolver : resolvers) {
            Tuple2<String, String> t = new Tuple2<>(resolver.getIdResolver().toString(), resolver.getClassName());
            proxy.add(t);
        }
        Buffer<Tuple2<String, String>> resolverBuffer = JavaConversions.asScalaBuffer(proxy);
        scala.collection.immutable.List<Tuple2<String, String>> resolverList = resolverBuffer.toList();
        return resolverList;
    }
}
