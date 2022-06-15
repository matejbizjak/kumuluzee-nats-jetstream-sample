package si.matejbizjak.natsjetstream.sample.api.listener;


import com.kumuluz.ee.nats.common.annotations.ConfigurationOverride;
import com.kumuluz.ee.nats.common.annotations.ConsumerConfig;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;

/**
 * @author Matej Bizjak
 */

public class SimpleListener {

    @JetStreamListener(context = "context1", subject = "subject1")
    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliverPolicy", value = "new")})
    public void receive(String value) {
        System.out.println(value);
    }
}
