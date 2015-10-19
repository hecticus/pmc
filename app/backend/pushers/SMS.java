package backend.pushers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;

/**
 * Created by plessmann on 21/07/15.
 */
public class SMS extends Pusher {

    public SMS() {
    }

    public SMS(boolean insertResult) {
        super(insertResult);
    }

    @Override
    public ObjectNode push(ObjectNode event, Application app) {
        throw new UnsupportedOperationException("El metodo SMS no ha sido implementado");
    }
}
