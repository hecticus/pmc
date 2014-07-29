package backend.rabbitmq;

import com.rabbitmq.client.*;
import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.Connections;
import net.jodah.lyra.config.Config;
import net.jodah.lyra.config.RecoveryPolicies;
import net.jodah.lyra.config.RetryPolicy;
import net.jodah.lyra.util.Duration;
import utils.Utils;

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

    public static RabbitMQ getInstance() throws Exception {
        if(me == null){
            me = new RabbitMQ();
        }
        return me;
    }

    public RabbitMQ() throws Exception {
        host = models.basic.Config.getString("rabbit-mq-host");
        user = models.basic.Config.getString("rabbit-mq-user");
        password = models.basic.Config.getString("rabbit-mq-password");
        eventsQueue = models.basic.Config.getString("rabbit-mq-event-queue");
        pushQueue = models.basic.Config.getString("rabbit-mq-push-queue");
        resultQueue = models.basic.Config.getString("rabbit-mq-result-queue");
        Config config = new Config()
                .withRecoveryPolicy(RecoveryPolicies.recoverAlways())
                .withRetryPolicy(new RetryPolicy()
                        .withBackoff(Duration.seconds(1), Duration.seconds(30))
                        .withMaxDuration(Duration.minutes(10)));
        ConnectionOptions options = new ConnectionOptions().withHost(host)
                .withUsername(user)
                .withPassword(password).withName("RabbitMQ-"+System.currentTimeMillis());
        connection = Connections.create(options, config);
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
     * @throws Exception
     */
    public String getNextEventLyra() throws Exception {
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
            Utils.printToLog(RabbitMQ.class, null, "error", false, e, "support-level-1",models.basic.Config.LOGGER_ERROR);
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
        return event;
    }

    /**
     * Metodo para obtener el siguiente evento de la cola PUSH de RabbitMQ
     *
     * @return              string parseable a json con el evento a procesar
     * @throws Exception
     */
    public String getNextPushLyra() throws Exception {
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
            Utils.printToLog(RabbitMQ.class, null, "error", false, e, "support-level-1",models.basic.Config.LOGGER_ERROR);
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
        return event;
    }

    /**
     * Metodo para obtener el siguiente evento de la cola PUSH_RESULT de RabbitMQ
     *
     * @return              string parseable a json con el evento a procesar
     * @throws Exception
     */
    public String getNextPushResultLyra() throws Exception {
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
            Utils.printToLog(RabbitMQ.class, null, "error", false, e, "support-level-1",models.basic.Config.LOGGER_ERROR);
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
    public void insertEventLyra(String event) throws Exception {
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(eventsQueue, true, false, false, null);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).contentType("application/json").priority(1).build();
            channel.basicPublish("", eventsQueue, props, event.getBytes());
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, null, "error", false, e, "support-level-1",models.basic.Config.LOGGER_ERROR);
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
    public void insertPushLyra(String event) throws Exception {
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(pushQueue, true, false, false, null);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).contentType("application/json").priority(1).build();
            channel.basicPublish("", pushQueue, props, event.getBytes());
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, null, "error", false, e, "support-level-1",models.basic.Config.LOGGER_ERROR);
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
    public void insertPushResultLyra(String event) throws Exception {
        Channel channel = null;
        try{
            channel = connection.createChannel();
            channel.queueDeclare(resultQueue, true, false, false, null);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).contentType("application/json").priority(1).build();
            channel.basicPublish("", resultQueue, props, event.getBytes());
        }catch(Exception e){
            Utils.printToLog(RabbitMQ.class, null, "error", false, e, "support-level-1",models.basic.Config.LOGGER_ERROR);
        } finally {
            try{channel.close();}catch (Exception ex) {}
        }
    }
}
