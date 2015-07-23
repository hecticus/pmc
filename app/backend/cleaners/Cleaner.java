package backend.cleaners;

import backend.job.HecticusThread;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.apps.Application;
import models.apps.Device;

/**
 * Created by plessmann on 23/07/15.
 */
public abstract class Cleaner {

    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public abstract void clean(Application app, ObjectNode event, HecticusThread invoker);
}
