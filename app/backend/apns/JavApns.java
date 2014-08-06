package backend.apns;

import javapns.Push;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotifications;
import javapns.notification.transmission.PushQueue;
import models.apps.Application;
import models.basic.Config;
import utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase para manejar un pool de conexiones al APNS de IOS
 * Created by plesse on 7/30/14.
 */
public class JavApns {

    private static JavApns me = null;
    private Map<Long, PushQueue> connections = null;
    private int threads;


    public static JavApns getInstance() throws Exception {
        if(me == null){
            me = new JavApns();
        }
        return me;
    }

    public JavApns() {
        Utils.printToLog(JavApns.class, null, "Levantando JavApns", false, null, "support-level-1",models.basic.Config.LOGGER_INFO);
        this.connections = new HashMap<Long, PushQueue>();
        threads = Config.getInt("max-apns-threads");
        List<Application> apps = Application.finder.all();
        try{
            for(int i = 0; Utils.run.get() && i < apps.size(); ++i){
                if(apps.get(i).getActive() == 1){
                    File cert  = new File((apps.get(i).getIosSandbox() == 0 ? apps.get(i).getIosPushApnsCertProduction() : apps.get(i).getIosPushApnsCertSandbox()));
                    PushQueue queue = Push.queue(cert, apps.get(i).getIosPushApnsPassphrase(), apps.get(i).getIosSandbox() == 0, threads);
                    queue.start();
                    connections.put(apps.get(i).getIdApp(), queue);
                }
            }
        }catch(Exception e){}
        Utils.printToLog(JavApns.class, null, "Levantado JavApns con " + threads + " por app", false, null, "support-level-1",models.basic.Config.LOGGER_INFO);
    }

    /**
     * Metodo para encolar eventos de push
     *
     * @param app           Aplicacion asociada al evento a hacer push (se usa para buscar la cola existente o crear la nueva)
     * @param payload       Payload del evento a enviar
     * @param device        RegistrationIDs a quien se le enviara el paylod
     * @throws InvalidDeviceTokenFormatException
     * @throws KeystoreException
     */
    public void enqueue(Application app, PushNotificationPayload payload, String device) throws InvalidDeviceTokenFormatException, KeystoreException {
        long idApp = app.getIdApp();
        if(connections.containsKey(idApp)) {
            connections.get(idApp).add(payload, device);
        } else {
            File cert  = new File((app.getIosSandbox() == 0 ? app.getIosPushApnsCertProduction() : app.getIosPushApnsCertSandbox()));
            PushQueue queue = Push.queue(cert, app.getIosPushApnsPassphrase(), app.getIosSandbox() == 0, threads);
            queue.start();
            queue.add(payload, device);
            connections.put(idApp, queue);
        }
    }

    /**
     * Metodo para encolar eventos de push
     *
     * @param app           Aplicacion asociada al evento a hacer push (se usa para buscar la cola existente o crear la nueva)
     * @param payload       Payload del evento a enviar
     * @param devices       arreglo de string con los RegistrationIDs a quienes se le enviara el paylod
     * @throws InvalidDeviceTokenFormatException
     * @throws KeystoreException
     */
    public void enqueue(Application app, PushNotificationPayload payload, String[] devices) throws InvalidDeviceTokenFormatException, KeystoreException {
        long idApp = app.getIdApp();
        if(connections.containsKey(idApp)) {
            for(int i = 0; i < devices.length; ++i){
                connections.get(idApp).add(payload, devices[i]);
            }
        } else {
            File cert  = new File((app.getIosSandbox() == 0 ? app.getIosPushApnsCertProduction() : app.getIosPushApnsCertSandbox()));
            PushQueue queue = Push.queue(cert, app.getIosPushApnsPassphrase(), app.getIosSandbox() == 0, threads);
            queue.start();
            for(int i = 0; i < devices.length; ++i){
                queue.add(payload, devices[i]);
            }
            connections.put(idApp, queue);
        }
    }

    /**
     * Metodo para obtener los resultados de los push a IOS
     *
     * @param idApp     aplicacion a consultar
     * @return          objeto con los resultados
     */
    public PushedNotifications getPushedNotifications(long idApp){
        PushedNotifications pushedNotifications = connections.get(idApp).getPushedNotifications();
        connections.get(idApp).clearPushedNotifications();
        return pushedNotifications;
    }
}
