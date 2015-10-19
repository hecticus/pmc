package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.apps.Device;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Result;

import java.io.IOException;

import static play.data.Form.form;

import views.html.devices.*;

/**
 * Created by plessmann on 22/07/15.
 */
public class DevicesView extends HecticusController {

    final static Form<Device> DeviceViewForm = form(Device.class);
    public static Result GO_HOME = redirect(routes.DevicesView.list(0, "name", "asc", ""));

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result index() {
        return GO_HOME;
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result blank() {
        return ok(form.render(DeviceViewForm));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(list.render(Device.page(page, 10, sortBy, order, filter), sortBy, order, filter, false));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result edit(Long id) {
        Device objBanner = Device.finder.byId(id);
        Form<Device> filledForm = DeviceViewForm.fill(Device.finder.byId(id));
        return ok(edit.render(id, filledForm));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result update(Long id) {
        Form<Device> filledForm = DeviceViewForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(edit.render(id, filledForm));
        }
        Device gfilledForm = filledForm.get();
        gfilledForm.update(id);
        flash("success", Messages.get("devices.java.updated", gfilledForm.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result sort(String ids) {
        String[] aids = ids.split(",");

        for (int i=0; i<aids.length; i++) {
            Device oPost = Device.finder.byId(Long.parseLong(aids[i]));
            //oWoman.setSort(i);
            oPost.save();
        }

        return ok("Fine!");
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result lsort() {
        return ok(list.render(Device.page(0, 0, "configKey", "asc", ""),"date", "asc", "",true));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result delete(Long id) {
        Device instance = Device.finder.byId(id);
        instance.delete();
        flash("success", Messages.get("devices.java.deleted", instance.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result submit() throws IOException {
        Form<Device> filledForm = DeviceViewForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(form.render(filledForm));
        }

        Device gfilledForm = filledForm.get();
        gfilledForm.save();
        flash("success", Messages.get("devices.java.created", gfilledForm.getName()));
        return GO_HOME;

    }
}
