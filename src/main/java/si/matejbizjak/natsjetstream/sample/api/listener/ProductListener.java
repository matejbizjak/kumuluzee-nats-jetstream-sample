package si.matejbizjak.natsjetstream.sample.api.listener;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import si.matejbizjak.natsjetstream.sample.api.dto.Product;
import si.matejbizjak.natsjetstream.sample.api.subscriber.ProductSubscriber;

/**
 * @author Matej Bizjak
 */

public class ProductListener {

    private static final Logger LOG = LogManager.getLogger(ProductListener.class.getName());

    @JetStreamListener(connection = "secure", subject = "product.*")
//    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliver-policy", value = "new")})
    public void receive(Product product, JetStreamMessage msg) {
        LOG.info(String.format("Method receive received a product with description %s in subject %s."
                , product.getDescription(), msg.getSubject()));
    }
}
