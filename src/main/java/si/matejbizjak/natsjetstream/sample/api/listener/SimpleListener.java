package si.matejbizjak.natsjetstream.sample.api.listener;


import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;

/**
 * @author Matej Bizjak
 */

public class SimpleListener {

    @JetStreamListener(connection = "default", context = "context1", subject = "subject1")
    public void receive(String value) {
        System.out.println(value);
    }
}
