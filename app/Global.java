import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import backend.job.*;
import backend.job.test.HecticusGenerator;
//import backend.pushy.PushyManager;
import backend.rabbitmq.RabbitMQ;
import models.basic.Config;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by plesse on 7/10/14.
 */
public class Global extends GlobalSettings {

    public static AtomicBoolean run = null;
    HecticusThread supervisor = null;

    @Override
    public void onStart(Application application) {
        super.onStart(application);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(Config.getString("server-ip-file")));
            Utils.serverIp = br.readLine();
        } catch (Exception e) {
            Utils.serverIp = null;
            Utils.printToLog(Global.class, "Error cargando el IP del servidor", "Ocurrio un error cargando el IP del servidor desde el archivo. El PMC levantara pero no procesara eventos fallidos que esten en MySQL", false, null, "support-level-1", Config.LOGGER_ERROR);
        } finally {
            try {if (br != null)br.close();} catch (Exception ex) {}
        }
        Utils.printToLog(Global.class, null, "Arrancando " + Config.getString("app-name"), false, null, "support-level-1", Config.LOGGER_INFO);
        ActorSystem system = ActorSystem.create("application");
        run = new AtomicBoolean(true);
        Utils.run = run;
        Utils.printToLog(Global.class, null, "Arrancando ThreadSupervisor", false, null, "support-level-1", Config.LOGGER_INFO);
        supervisor = new ThreadSupervisor(run, system);
        Cancellable cancellable = system.scheduler().schedule(Duration.create(1, SECONDS), Duration.create(5, MINUTES), supervisor, system.dispatcher());
        supervisor.setCancellable(cancellable);
    }

    @Override
    public void onStop(Application application) {
        super.onStop(application);
        run.set(false);
        Utils.printToLog(Global.class, "Apagando " + Config.getString("app-name"), "Apagando " + Config.getString("app-name")+", se recibio la señal de shutdown", true, null, "support-level-1", Config.LOGGER_INFO);
        supervisor.cancel();
    }

    @SuppressWarnings("rawtypes")
    Action newAction = new Action.Simple() {
        @Override
        public F.Promise<Result> call(Http.Context ctx) throws Throwable {
            // TODO Auto-generated method stub
            Logger.info("Calling action for " + ctx);
            return delegate.call(ctx);
        }
    };

    @SuppressWarnings("rawtypes")
    @Override
    public Action onRequest(Http.Request request, Method actionMethod) {
        String ipString = request.remoteAddress();
        String invoker = actionMethod.getDeclaringClass().getName();
        String[] octetos = ipString.split("\\.");
        if(invoker.startsWith("controllers.apps") || invoker.startsWith("controllers.Application") || invoker.startsWith("controllers.events")){
            if(ipString.equals("127.0.0.1") || ipString.startsWith("10.0.3")
                    || (ipString.startsWith("10.182.") && Integer.parseInt(octetos[2]) <= 127 )
                    || ipString.startsWith("10.181.")
                    || ipString.startsWith("10.208.")
                    || request.path().endsWith(".hecticus.com")
                    || request.path().equals("190.14.219.174")
                    || request.path().equals("201.249.204.73")
                    || request.path().equals("186.74.13.178")){
                if(!request.path().equals("10.182.7.53")){
                    Logger.info("Pass request from "+ipString+" to "+invoker);
                }
                return super.onRequest(request, actionMethod);
            }else{
                Logger.info("Deny request from "+ipString+" to "+invoker);
                return newAction;
            }
        }else{
            Logger.info("Deny request from "+ipString+" to "+invoker);
            return newAction;
        }
    }
}
