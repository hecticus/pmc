package backend.resolvers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;


/**
 * Created by plesse on 12/1/14.
 */
public abstract class Resolver {

    public abstract ObjectNode resolve(ObjectNode event, Application app);

}