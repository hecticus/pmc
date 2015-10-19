package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.apps.Resolver;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Result;

import java.io.IOException;

import static play.data.Form.form;

import views.html.resolvers.*;

/**
 * Created by plessmann on 22/07/15.
 */
public class ResolversView extends HecticusController {

    final static Form<Resolver> ResolverViewForm = form(Resolver.class);
    public static Result GO_HOME = redirect(routes.ResolversView.list(0, "name", "asc", ""));

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result index() {
        return GO_HOME;
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result blank() {
        return ok(form.render(ResolverViewForm));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(list.render(Resolver.page(page, 10, sortBy, order, filter), sortBy, order, filter, false));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result edit(Long id) {
        Resolver objBanner = Resolver.finder.byId(id);
        Form<Resolver> filledForm = ResolverViewForm.fill(Resolver.finder.byId(id));
        return ok(edit.render(id, filledForm));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result update(Long id) {
        Form<Resolver> filledForm = ResolverViewForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(edit.render(id, filledForm));
        }
        Resolver gfilledForm = filledForm.get();
        gfilledForm.update(id);
        flash("success", Messages.get("resolvers.java.updated", gfilledForm.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result sort(String ids) {
        String[] aids = ids.split(",");

        for (int i=0; i<aids.length; i++) {
            Resolver oPost = Resolver.finder.byId(Long.parseLong(aids[i]));
            //oWoman.setSort(i);
            oPost.save();
        }

        return ok("Fine!");
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result lsort() {
        return ok(list.render(Resolver.page(0, 0, "configKey", "asc", ""),"date", "asc", "",true));
    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result delete(Long id) {
        Resolver instance = Resolver.finder.byId(id);
        instance.delete();
        flash("success", Messages.get("resolvers.java.deleted", instance.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.ADMIN_ROLE))
    public static Result submit() throws IOException {
        Form<Resolver> filledForm = ResolverViewForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(form.render(filledForm));
        }

        Resolver gfilledForm = filledForm.get();
        gfilledForm.save();
        flash("success", Messages.get("resolvers.java.created", gfilledForm.getName()));
        return GO_HOME;

    }
}
