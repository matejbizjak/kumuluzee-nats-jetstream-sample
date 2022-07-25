package si.matejbizjak.natsjetstream.sample.api.listener;


import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.common.annotations.ConfigurationOverride;
import com.kumuluz.ee.nats.common.annotations.ConsumerConfig;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import si.matejbizjak.natsjetstream.sample.api.subscriber.TextSubscriber;

/**
 * @author Matej Bizjak
 */

public class TextListener {

    private static final Logger LOG = LogManager.getLogger(TextListener.class.getName());

    @JetStreamListener(context = "context1", subject = "subject1")
    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliver-policy", value = "new")})
    public void receive(String value) {
        LOG.info(String.format("Method receive received a message %s in subject subject1.", value));
    }
}
