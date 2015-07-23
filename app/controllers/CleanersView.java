package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.apps.Cleaner;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Result;

import java.io.IOException;

import static play.data.Form.form;

import views.html.cleaners.*;

/**
 * Created by plessmann on 23/07/15.
 */
public class CleanersView extends HecticusController {

    final static Form<Cleaner> CleanerViewForm = form(Cleaner.class);
    public static Result GO_HOME = redirect(routes.CleanersView.list(0, "name", "asc", ""));

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result index() {
        return GO_HOME;
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result blank() {
        return ok(form.render(CleanerViewForm));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(list.render(Cleaner.page(page, 10, sortBy, order, filter), sortBy, order, filter, false));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result edit(Long id) {
        Cleaner objBanner = Cleaner.finder.byId(id);
        Form<Cleaner> filledForm = CleanerViewForm.fill(Cleaner.finder.byId(id));
        return ok(edit.render(id, filledForm));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result update(Long id) {
        Form<Cleaner> filledForm = CleanerViewForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(edit.render(id, filledForm));
        }
        Cleaner gfilledForm = filledForm.get();
        gfilledForm.update(id);
        flash("success", Messages.get("cleaners.java.updated", gfilledForm.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result sort(String ids) {
        String[] aids = ids.split(",");

        for (int i=0; i<aids.length; i++) {
            Cleaner oPost = Cleaner.finder.byId(Long.parseLong(aids[i]));
            //oWoman.setSort(i);
            oPost.save();
        }

        return ok("Fine!");
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result lsort() {
        return ok(list.render(Cleaner.page(0, 0, "configKey", "asc", ""),"date", "asc", "",true));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result delete(Long id) {
        Cleaner instance = Cleaner.finder.byId(id);
        instance.delete();
        flash("success", Messages.get("cleaners.java.deleted", instance.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result submit() throws IOException {
        Form<Cleaner> filledForm = CleanerViewForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(form.render(filledForm));
        }

        Cleaner gfilledForm = filledForm.get();
        gfilledForm.save();
        flash("success", Messages.get("cleaners.java.created", gfilledForm.getName()));
        return GO_HOME;

    }
}
