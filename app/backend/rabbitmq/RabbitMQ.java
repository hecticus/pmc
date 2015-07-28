package backend.rabbitmq;

import backend.ServerInstance;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import models.basic.Event;
import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.Connections;
import net.jodah.lyra.config.Config;
import net.jodah.lyra.config.RecoveryPolicies;
import net.jodah.lyra.config.RetryPolicy;
import net.jodah.lyra.util.Duration;
import utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Clase para manejar la conexion a RabbitMQ y los metodos para insertar y consumir de las colas
 *
 * Created by plesse on 7/11/14.
 */
public class RabbitMQ {
    private static RabbitMQ me = null;
    private Connection connection = null;
    private String host;
    private String user;
    private String password;
    private String eventsQueue;
    private String pushQueue;
    private String resultQueue;
    private String failedRoute;

    public static synchronized RabbitMQ getInstance() throws Exception {
        if(me == null){
            me = new RabbitMQ();
        }
        return me;
    }

    public RabbitMQ() throws Exception {
        failedRoute = models.Config.getString("failed-events-files-route");
        host = models.Config.getString("rabbit-mq-host");
        user = models.Config.getString("rabbit-mq-user");
        password = models.Config.getString("rabbit-mq-password");
        eventsQueue = models.Config.getString("rabbit-mq-event-queue");
        pushQueue = models.Config.getString("rabbit-mq-push-queue");
        resultQueue = models.Config.getString("rabbit-mq-result-queue");
        Utils.printToLog(RabbitMQ.class, null, "Levantando RabbitMQ hacia " + host + " con el User " + user, false, null, "support-level-1",models.Config.LOGGER_INFO);
        Config config = new Config()
                .withRecoveryPolicy(RecoveryPolicies.recoverAlways())
                .withRetryPolicy(new RetryPolicy()
                        .withBackoff(Duration.seconds(30), Duration.seconds(120))
                        .withMaxDuration(Duration.minutes(10)));
        ConnectionOptions options = new ConnectionOptions().withHost(host)
                .withUsername(user)
                .withPassword(password).withName("RabbitMQ-"+System.currentTimeMillis());
        connection = Connections.create(options, config);
        Utils.printToLog(RabbitMQ.class, null, "Conectado RabbitMQ con " + host + " con el User " + user, false, null, "support-level-1",models.Config.LOGGER_INFO);
    }

    /**
     * Metodo para cerrar la conexion a RabbitMQ
     */
    public void closeInstance(){
        try{
            if(me != null){
                connection.close();
            }
        }catch (Exception ex) {

        }
    }

    /**
     * Metodo para obtener el siguiente evento de la cola EVENTS de RabbitMQ
     *
     * @return              string parseable a json con el evento a procesar
     *
     */
    public String getNextEventLyra() {
        String event = null;
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(eventsQueue, true, false, false, null);
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(eventsQueue, false, "RabbitMQ-Event-"+System.currentTimeMillis(), consumer);
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if(delivery != null) {
                event = new String(delivery.getBody());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en la conexion a RabbitMQ", "Error en la conexion a RabbitMQ obteniendo eventos de " + eventsQueue, true, e, "support-level-1",models.Config.LOGGER_ERROR);
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
        return event;
    }

    /**
     * Metodo para obtener el siguiente evento de la cola PUSH de RabbitMQ
     *
     * @return              string parseable a json con el evento a procesar
     *
     */
    public String getNextPushLyra() {
        String event = null;
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(pushQueue, true, false, false, null);
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(pushQueue, false, "RabbitMQ-Push-"+System.currentTimeMillis(), consumer);
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if(delivery != null) {
                event = new String(delivery.getBody());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en la conexion a RabbitMQ", "Error en la conexion a RabbitMQ obteniendo eventos de " + pushQueue, true, e, "support-level-1",models.Config.LOGGER_ERROR);
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
        return event;
    }

    /**
     * Metodo para obtener el siguiente evento de la cola PUSH_RESULT de RabbitMQ
     *
     * @return              string parseable a json con el evento a procesar
     *
     */
    public String getNextPushResultLyra() {
        String event = null;
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(resultQueue, true, false, false, null);
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(resultQueue, false, "RabbitMQ-Result-"+System.currentTimeMillis(), consumer);
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if(delivery != null) {
                event = new String(delivery.getBody());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en la conexion a RabbitMQ", "Error en la conexion a RabbitMQ obteniendo eventos de " + resultQueue, true, e, "support-level-1",models.Config.LOGGER_ERROR);
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
        return event;
    }

    /**
     * Metodo para insertar eventos en la cola EVENTS de RabbitMQ
     *
     * @param event         string parseable a json con el evento a insertar
     * @throws Exception
     */
    public void insertEventLyra(String event) {
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(eventsQueue, true, false, false, null);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).contentType("application/json").priority(1).build();
            channel.basicPublish("", eventsQueue, props, event.getBytes());
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en la conexion a RabbitMQ", "Error en la conexion a RabbitMQ insertando eventos en " + eventsQueue, true, e, "support-level-1",models.Config.LOGGER_ERROR);
            insertObjectInDB("event", event);
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
    }

    /**
     * Metodo para insertar eventos en la cola PUSH de RabbitMQ
     *
     * @param event         string parseable a json con el evento a insertar
     * @throws Exception
     */
    public void insertPushLyra(String event) {
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(pushQueue, true, false, false, null);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).contentType("application/json").priority(1).build();
            channel.basicPublish("", pushQueue, props, event.getBytes());
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en la conexion a RabbitMQ", "Error en la conexion a RabbitMQ insertando eventos en " + pushQueue, true, e, "support-level-1",models.Config.LOGGER_ERROR);
            insertObjectInDB("push", event);
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
    }

    /**
     * Metodo para insertar eventos en la cola PUSH_RESULT de RabbitMQ
     *
     * @param event         string parseable a json con el evento a insertar
     * @throws Exception
     */
    public void insertPushResultLyra(String event) {
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(resultQueue, true, false, false, null);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).contentType("application/json").priority(1).build();
            channel.basicPublish("", resultQueue, props, event.getBytes());
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en la conexion a RabbitMQ", "Error en la conexion a RabbitMQ insertando eventos en " + resultQueue, true, e, "support-level-1",models.Config.LOGGER_ERROR);
            insertObjectInDB("result", event);
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
    }

    /**
     * Metodo para insertar eventos en la cola EVENTS de RabbitMQ
     *
     * @param event         string parseable a json con el evento a insertar
     * @throws Exception
     */
    public boolean insertEventLyraWithResult(String event) {
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(eventsQueue, true, false, false, null);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).contentType("application/json").priority(1).build();
            channel.basicPublish("", eventsQueue, props, event.getBytes());
            return true;
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en la conexion a RabbitMQ", "Error en la conexion a RabbitMQ insertando eventos en " + eventsQueue, true, e, "support-level-1",models.Config.LOGGER_ERROR);
            return false;
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
    }

    /**
     * Metodo para insertar eventos en la cola PUSH de RabbitMQ
     *
     * @param event         string parseable a json con el evento a insertar
     * @throws Exception
     */
    public boolean insertPushLyraWithResult(String event) {
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(pushQueue, true, false, false, null);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).contentType("application/json").priority(1).build();
            channel.basicPublish("", pushQueue, props, event.getBytes());
            return true;
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en la conexion a RabbitMQ", "Error en la conexion a RabbitMQ insertando eventos en " + pushQueue, true, e, "support-level-1",models.Config.LOGGER_ERROR);
            return false;
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
    }

    /**
     * Metodo para insertar eventos en la cola PUSH_RESULT de RabbitMQ
     *
     * @param event         string parseable a json con el evento a insertar
     * @throws Exception
     */
    public boolean insertPushResultLyraWithResult(String event) {
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(resultQueue, true, false, false, null);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).contentType("application/json").priority(1).build();
            channel.basicPublish("", resultQueue, props, event.getBytes());
            return true;
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en la conexion a RabbitMQ", "Error en la conexion a RabbitMQ insertando eventos en " + resultQueue, true, e, "support-level-1",models.Config.LOGGER_ERROR);
            return false;
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
    }

    private void insertObjectInDB(String type, String event) {
        try{
            String name = failedRoute + type + "-" + System.currentTimeMillis() + ".txt";
            File file = new File(name);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(event);
            bw.close();
            Event e = new Event(type, name, ServerInstance.getInstance().getRealInstance());
            Event.save(e);
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, "Error en Guardando evento en MySQL", "Error el evento no se pudo guardar en MySQL luego de fallar en RabbitMQ cola:" + type + ", evento: " + event, true, e, "support-level-1", models.Config.LOGGER_ERROR);
        }
    }
}
