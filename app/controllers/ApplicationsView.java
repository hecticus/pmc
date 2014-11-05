package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;

import static play.data.Form.form;

import utils.Utils;
import views.html.apps.*;
/**
 * Created by plesse on 11/4/14.
 */
public class ApplicationsView extends HecticusController {

    final static Form<models.apps.Application> ApplicationViewForm = form(models.apps.Application.class);
    public static Result GO_HOME = redirect(routes.ApplicationsView.list(0, "name", "asc", ""));

    @Restrict(@Group(Application.USER_ROLE))
    public static Result index() {
        return GO_HOME;
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result blank() {
        return ok(form.render(ApplicationViewForm));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(list.render(models.apps.Application.page(page, 10, sortBy, order, filter), sortBy, order, filter, false));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result edit(Long id) {
        Form<models.apps.Application> filledForm = ApplicationViewForm.fill(models.apps.Application.finder.byId(id));
        return ok(edit.render(id, filledForm));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result update(Long id) {
        Form<models.apps.Application> filledForm = ApplicationViewForm.bindFromRequest();
//        System.out.println(filledForm.toString());
        if(filledForm.hasErrors()) {
            return badRequest(edit.render(id, filledForm));
        }
        models.apps.Application app = models.apps.Application.finder.byId(id);
        String sandbox = null;
        String production = null;

        if (filledForm.data().containsKey("iosPushApnsCertSandbox")) {
            String certificate = filledForm.data().get("iosPushApnsCertSandbox");
            if (!app.getIosPushApnsCertSandbox().equalsIgnoreCase(certificate)) {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                try {
                    Http.MultipartFormData.FilePart cert = body.getFile("iosPushApnsCertSandbox");
                    File file = cert.getFile();
                    sandbox = Utils.uploadCertificate(file, app.getName().replace(" ", ""), certificate);
                } catch (Exception e) {
                    filledForm.reject(Messages.get("applications.java.fileError"), Messages.get("applications.java.fileError.iosSandbox"));
                    return badRequest(edit.render(id, filledForm));
                }
            }
        }

        if (filledForm.data().containsKey("iosPushApnsCertProduction")) {
            String certificate = filledForm.data().get("iosPushApnsCertProduction");
            if (!app.getIosPushApnsCertProduction().equalsIgnoreCase(certificate)) {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                try{
                    Http.MultipartFormData.FilePart cert = body.getFile("iosPushApnsCertProduction");
                    File file = cert.getFile();
                    production = Utils.uploadCertificate(file, app.getName().replace(" ", ""), certificate);
                } catch(Exception e){
                    filledForm.reject(Messages.get("applications.java.fileError"), Messages.get("applications.java.fileError.iosProduction"));
                    return badRequest(edit.render(id, filledForm));
                }
            }
        }
        String name = filledForm.data().get("name");
        if (!app.getName().equalsIgnoreCase(name)) {
            try {
                String oldName = app.getName().replace(" ", "");
                name = name.replace(" ", "");
                Utils.renameCertificateFolder(app.getName().replace(" ", ""), name.replace(" ", ""));
                if(sandbox == null){
                    sandbox = app.getIosPushApnsCertSandbox();
                }
                sandbox = sandbox.replace(oldName, name);

                if(production == null){
                    production = app.getIosPushApnsCertProduction();
                }
                production = production.replace(oldName, name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        models.apps.Application gfilledForm = filledForm.get();
        if(sandbox != null && !sandbox.isEmpty()){
            gfilledForm.setIosPushApnsCertSandbox(sandbox);
        }

        if(production != null && !production.isEmpty()){
            gfilledForm.setIosPushApnsCertProduction(production);
        }

        gfilledForm.update(id);
        flash("success", Messages.get("applications.java.updated", gfilledForm.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result sort(String ids) {
        String[] aids = ids.split(",");

        for (int i=0; i<aids.length; i++) {
            models.apps.Application oPost = models.apps.Application.finder.byId(Long.parseLong(aids[i]));
            //oWoman.setSort(i);
            oPost.save();
        }

        return ok("Fine!");
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result lsort() {
        return ok(list.render(models.apps.Application.page(0, 0, "name", "asc", ""),"date", "asc", "",true));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result delete(Long id) {
        models.apps.Application app = models.apps.Application.finder.byId(id);
        try {
            Utils.deleteCertificateFolder(app.getName().replace(" ", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        app.delete();
        flash("success", Messages.get("applications.java.deleted", app.getName()));
        return GO_HOME;

    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result submit() throws IOException {
        Form<models.apps.Application> filledForm = ApplicationViewForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            System.out.println(filledForm.toString());
            return badRequest(form.render(filledForm));
        }

        String sandbox = null;
        String production = null;
        String name = filledForm.data().get("name");

        if (filledForm.data().containsKey("iosPushApnsCertSandbox")) {
            String certificate = filledForm.data().get("iosPushApnsCertSandbox");
            if (certificate != null && !certificate.isEmpty()) {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                try {
                    Http.MultipartFormData.FilePart cert = body.getFile("iosPushApnsCertSandbox");
                    File file = cert.getFile();
                    sandbox = Utils.uploadCertificate(file, name.replace(" ", ""), certificate);
                } catch(Exception e){
                    filledForm.reject(Messages.get("applications.java.fileError"), Messages.get("applications.java.fileError.iosSandbox"));
                    return badRequest(form.render(filledForm));
                }
            }
        }

        if (filledForm.data().containsKey("iosPushApnsCertProduction")) {
            String certificate = filledForm.data().get("iosPushApnsCertProduction");
            if (certificate != null && !certificate.isEmpty()) {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                try {
                    Http.MultipartFormData.FilePart cert = body.getFile("iosPushApnsCertProduction");
                    File file = cert.getFile();
                    production = Utils.uploadCertificate(file, name.replace(" ", ""), certificate);
                } catch(Exception e){
                    filledForm.reject(Messages.get("applications.java.fileError"), Messages.get("applications.java.fileError.iosProduction"));
                    return badRequest(form.render(filledForm));
                }
            }
        }

        models.apps.Application gfilledForm = filledForm.get();

        if(sandbox != null && !sandbox.isEmpty()){
            gfilledForm.setIosPushApnsCertSandbox(sandbox);
        }

        if(production != null && !production.isEmpty()){
            gfilledForm.setIosPushApnsCertProduction(production);
        }

        gfilledForm.save();
        flash("success", Messages.get("applications.java.created", gfilledForm.getName()));
        return GO_HOME;

    }
}
