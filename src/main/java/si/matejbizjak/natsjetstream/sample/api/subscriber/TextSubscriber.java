package si.matejbizjak.natsjetstream.sample.api.subscriber;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.common.annotations.ConfigurationOverride;
import com.kumuluz.ee.nats.common.annotations.ConsumerConfig;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamSubscriber;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * @author Matej Bizjak
 */

@ApplicationScoped
public class TextSubscriber {

    private static final Logger LOG = LogManager.getLogger(TextSubscriber.class.getName());

    @Inject
    @JetStreamSubscriber(context = "context1", stream = "stream1", subject = "subject2", durable = "textSubscriber")
    @ConsumerConfig(base = "customConsumer", configOverrides = {@ConfigurationOverride(key = "deliver-policy", value = "new")})
    private JetStreamSubscription jetStreamSubscription;

    public void pullMsg() {
        if (jetStreamSubscription != null) {
            List<Message> messages = jetStreamSubscription.fetch(3, Duration.ofSeconds(1));
            for (Message message : messages) {
                try {
                    LOG.info(String.format("Method pullMsg received (pull) message %s in subject subject2."
                            , SerDes.deserialize(message.getData(), String.class)));
                    message.ack();
                } catch (IOException e) {
                    message.nak();
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
