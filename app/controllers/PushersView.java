package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.apps.Pusher;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Result;

import java.io.IOException;

import static play.data.Form.form;

import views.html.pushers.*;

/**
 * Created by plessmann on 22/07/15.
 */
public class PushersView extends HecticusController {

    final static Form<Pusher> PusherViewForm = form(Pusher.class);
    public static Result GO_HOME = redirect(routes.PushersView.list(0, "name", "asc", ""));

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result index() {
        return GO_HOME;
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result blank() {
        return ok(form.render(PusherViewForm));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(list.render(Pusher.page(page, 10, sortBy, order, filter), sortBy, order, filter, false));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result edit(Long id) {
        Pusher objBanner = Pusher.finder.byId(id);
        Form<Pusher> filledForm = PusherViewForm.fill(Pusher.finder.byId(id));
        return ok(edit.render(id, filledForm));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result update(Long id) {
        Form<Pusher> filledForm = PusherViewForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(edit.render(id, filledForm));
        }
        Pusher gfilledForm = filledForm.get();
        gfilledForm.update(id);
        flash("success", Messages.get("pushers.java.updated", gfilledForm.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result sort(String ids) {
        String[] aids = ids.split(",");

        for (int i=0; i<aids.length; i++) {
            Pusher oPost = Pusher.finder.byId(Long.parseLong(aids[i]));
            //oWoman.setSort(i);
            oPost.save();
        }

        return ok("Fine!");
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result lsort() {
        return ok(list.render(Pusher.page(0, 0, "configKey", "asc", ""),"date", "asc", "",true));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result delete(Long id) {
        Pusher instance = Pusher.finder.byId(id);
        instance.delete();
        flash("success", Messages.get("pushers.java.deleted", instance.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result submit() throws IOException {
        Form<Pusher> filledForm = PusherViewForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(form.render(filledForm));
        }

        Pusher gfilledForm = filledForm.get();
        gfilledForm.save();
        flash("success", Messages.get("pushers.java.created", gfilledForm.getName()));
        return GO_HOME;

    }
}
