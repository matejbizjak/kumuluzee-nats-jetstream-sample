package si.matejbizjak.natsjetstream.sample.api.subscriber;

import com.kumuluz.ee.nats.common.annotations.ConfigurationOverride;
import com.kumuluz.ee.nats.common.annotations.ConsumerConfig;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamSubscriber;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;
import si.matejbizjak.natsjetstream.sample.api.entity.Demo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Matej Bizjak
 */

@ApplicationScoped
public class ComplexSubscriber {

    @Inject
    @JetStreamSubscriber(connection = "secure", subject = "subject2", durable = "onlyNew")
    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliverPolicy", value = "new")})
    private JetStreamSubscription jetStreamSubscription;

    public void pullMsg() {
        if (jetStreamSubscription != null) {
            List<Message> messages = jetStreamSubscription.fetch(3, Duration.ofSeconds(1));
            for (Message message : messages) {
                try {
                    System.out.println(message.getSID());
                    System.out.println(message.getHeaders());
                    LocalDateTime entity = SerDes.deserialize(message.getData(), LocalDateTime.class);
                    System.out.println(entity.toString());
                    message.ack();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
