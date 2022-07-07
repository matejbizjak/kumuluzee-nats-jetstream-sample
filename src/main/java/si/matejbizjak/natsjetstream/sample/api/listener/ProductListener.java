package si.matejbizjak.natsjetstream.sample.api.listener;

import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import si.matejbizjak.natsjetstream.sample.api.entity.Product;

/**
 * @author Matej Bizjak
 */

public class ProductListener {

    @JetStreamListener(connection = "secure", subject = "product.*")
//    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliver-policy", value = "new")})
    public void receive(Product entity, JetStreamMessage msg) {
        System.out.printf("'%s' at subject: %s%n", entity.getName(), msg.getSubject());
    }
}
