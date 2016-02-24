package synyx.coffee;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String QUEUE_NAME = "DEMO";

    private static String getMessage(String[] messageParts) {
        if(messageParts.length < 1) {
            return "hello world!";
        }
        else {
            return joinStrings(messageParts, " ");
        }
    }

    private static String joinStrings(String[] messageParts, String delimiter) {
        if(messageParts.length == 0) {
            return "";
        }
        else {
            StringBuilder builder = new StringBuilder(messageParts[0]);
            for (int i=1; i < messageParts.length; ++i) {
                builder.append(delimiter).append(messageParts[i]);
            }
            return builder.toString();
        }
    }

    public static void main( String[] args ) throws IOException, TimeoutException
    {
        Optional<Connection > conn = Optional.empty();
        Optional<Channel> channel = Optional.empty();

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.99.100");
            conn = Optional.of(factory.newConnection());
            channel = Optional.of(conn.get().createChannel());
            channel.get().queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = getMessage(args);
            channel.get().basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "' to queue " + QUEUE_NAME);
        }
        finally {
            if(channel.isPresent()) {
                channel.get().close();
            }
            if(conn.isPresent()) {
                conn.get().close();
            }
        }
    }
}
