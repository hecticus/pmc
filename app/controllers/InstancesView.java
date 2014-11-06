package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.basic.Instance;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Result;

import java.io.IOException;

import static play.data.Form.form;

import views.html.instances.*;

/**
 * Created by plesse on 11/4/14.
 */
public class InstancesView extends HecticusController {

    final static Form<Instance> InstanceViewForm = form(Instance.class);
    public static Result GO_HOME = redirect(routes.InstancesView.list(0, "name", "asc", ""));

    @Restrict(@Group(Application.USER_ROLE))
    public static Result index() {
        return GO_HOME;
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result blank() {
        return ok(form.render(InstanceViewForm));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(list.render(Instance.page(page, 10, sortBy, order, filter), sortBy, order, filter, false));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result edit(Long id) {
        Instance objBanner = Instance.finder.byId(id);
        Form<Instance> filledForm = InstanceViewForm.fill(Instance.finder.byId(id));
        return ok(edit.render(id, filledForm));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result update(Long id) {
        Form<Instance> filledForm = InstanceViewForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(edit.render(id, filledForm));
        }
        Instance gfilledForm = filledForm.get();
        gfilledForm.update(id);
        flash("success", Messages.get("instances.java.updated", gfilledForm.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result sort(String ids) {
        String[] aids = ids.split(",");

        for (int i=0; i<aids.length; i++) {
            Instance oPost = Instance.finder.byId(Long.parseLong(aids[i]));
            //oWoman.setSort(i);
            oPost.save();
        }

        return ok("Fine!");
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result lsort() {
        return ok(list.render(Instance.page(0, 0, "configKey", "asc", ""),"date", "asc", "",true));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result delete(Long id) {
        Instance instance = Instance.finder.byId(id);
        instance.delete();
        flash("success", Messages.get("instances.java.deleted", instance.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result submit() throws IOException {
        Form<Instance> filledForm = InstanceViewForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(form.render(filledForm));
        }

        Instance gfilledForm = filledForm.get();
        gfilledForm.save();
        flash("success", Messages.get("instances.java.created", gfilledForm.getName()));
        return GO_HOME;

    }
}