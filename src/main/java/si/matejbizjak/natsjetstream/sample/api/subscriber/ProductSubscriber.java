package si.matejbizjak.natsjetstream.sample.api.subscriber;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.common.annotations.ConfigurationOverride;
import com.kumuluz.ee.nats.common.annotations.ConsumerConfig;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamSubscriber;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;
import si.matejbizjak.natsjetstream.sample.api.dto.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * @author Matej Bizjak
 */

@ApplicationScoped
public class ProductSubscriber {

    private static final Logger LOG = LogManager.getLogger(ProductSubscriber.class.getName());

    @Inject
    @JetStreamSubscriber(connection = "secure", subject = "product.corn", durable = "newCorn")
    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliver-policy", value = "new")})
    private JetStreamSubscription jetStreamSubscription;

    public void pullCorn() {
        if (jetStreamSubscription != null) {
            List<Message> messages = jetStreamSubscription.fetch(3, Duration.ofSeconds(1));
            for (Message message : messages) {
                try {
                    Product corn = SerDes.deserialize(message.getData(), Product.class);
                    LOG.info(String.format("Method pullCorn received (pull) a corn with description %s in subject product.corn. The message also contains header Nats-Msg-Id with value: %s"
                            , corn.getDescription(), message.getHeaders().get("Nats-Msg-Id")));
                    message.ack();
                } catch (IOException e) {
                    message.nak();
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
