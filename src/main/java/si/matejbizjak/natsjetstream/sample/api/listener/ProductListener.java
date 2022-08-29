package si.matejbizjak.natsjetstream.sample.api.listener;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import si.matejbizjak.natsjetstream.sample.api.dto.Product;
import si.matejbizjak.natsjetstream.sample.api.subscriber.ProductSubscriber;

import java.util.List;
import java.util.Map;

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

    @JetStreamListener(connection = "secure", subject = "map.products")
    public void receiveMap(Map<String, List<Product>> productsMap, JetStreamMessage msg) {
        LOG.info(String.format("Method receiveMap received a productsMap with keys %s. The first product of key = '1' is %s, while the second product of key '2' is %s."
                , productsMap.keySet(), productsMap.get("1").get(0).getName(), productsMap.get("2").get(1).getName()));
    }
}
