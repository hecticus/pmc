package controllers.basic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.basic.Config;
import play.Play;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by plesse on 8/15/14.
 */
public class Secured extends Security.Authenticator {

    public final static String AUTH_TOKEN_HEADER = "HECTICUS-X-AUTH-TOKEN";

    /**
     * Metodo para definir la validacion del token donde se envie el md5 de autenticacion
     *
     * @param ctx       context de la aplicacion para minar el header
     * @return          null para rechazar y cualquier string para aceptar
     */
    @Override
    public String getUsername(Http.Context ctx) {
        boolean secured = false;
        try {
            secured = Config.getInt("secured-access") == 1;
        } catch (Exception e){}
        if(secured) {
            String[] authTokenHeaderValues = ctx.request().headers().get(AUTH_TOKEN_HEADER);
            if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
                String md5 = getAuthToken();
                if (authTokenHeaderValues[0].equals(md5)) {
                    Utils.printToLog(Secured.class, null, "Valid Token", false, null, "support-level-1", Config.LOGGER_INFO);
                    return "OK";
                }
                Utils.printToLog(Secured.class, null, "Invalid Token " + authTokenHeaderValues[0], false, null, "support-level-1", Config.LOGGER_INFO);
            } else {
                Utils.printToLog(Secured.class, null, "Missing Header " + AUTH_TOKEN_HEADER, false, null, "support-level-1", Config.LOGGER_INFO);
            }
            return null;
        } else {
            return "OK";
        }
    }

    /**
     * Metodo para manejar los request rechazados por no tener el token de autenticacion o poner tener el token incorrecto
     *
     * @param ctx       contexto http (obligatorio)
     * @return          respuesta a dar al usuario rechazado
     */
    @Override
    public Result onUnauthorized(Http.Context ctx) {
        ObjectNode response = Json.newObject();
        response.put("error", 1);
        response.put("description", "Invalid User");
        return forbidden(response);
    }

    public static String getAuthToken(){
        String secret = Play.application().configuration().getString("application.secret");
        SimpleDateFormat sfaux = new SimpleDateFormat("yyyMMdd");
        Calendar today = new GregorianCalendar(TimeZone.getDefault());
        String md5 = Utils.generateMD5(secret + "-" + sfaux.format(today.getTime()));
        return md5;
    }

}
