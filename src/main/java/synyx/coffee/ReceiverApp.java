package synyx.coffee;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Created by jayasinghe on 22/02/16.
 */
public class ReceiverApp {
    private static final String QUEUE_NAME = "DEMO";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Optional<Connection > conn = Optional.empty();
        Optional<Channel> channel = Optional.empty();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.99.100");

        conn = Optional.of(factory.newConnection());
        channel = Optional.of(conn.get().createChannel());

        channel.get().queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("waiting for messages.");

        Consumer firstConsumer = new DefaultConsumer(channel.get()) {
            public void handleDelivery(String s, Envelope envelope,
                   AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                String receivedMsg = new String(bytes, "UTF-8");
                System.out.println("received message " + receivedMsg);
                try {
                    doWork(receivedMsg);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    System.out.println("done! :)");
                }
            }
        };
        channel.get().basicConsume(QUEUE_NAME, true, firstConsumer);
    }

    private static void doWork(String task) throws InterruptedException {
        for (char ch: task.toCharArray()) {
            if (ch == '.') Thread.sleep(1000);
        }
    }
}