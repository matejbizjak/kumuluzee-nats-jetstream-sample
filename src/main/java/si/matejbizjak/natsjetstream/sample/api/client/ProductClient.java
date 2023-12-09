package si.matejbizjak.natsjetstream.sample.api.client;

import com.kumuluz.ee.nats.jetstream.annotations.JetStreamSubject;
import com.kumuluz.ee.nats.jetstream.annotations.RegisterJetStreamClient;
import io.nats.client.api.PublishAck;
import si.matejbizjak.natsjetstream.sample.api.dto.Product;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RegisterJetStreamClient(connection = "secure")
public interface ProductClient {

    @JetStreamSubject(value = "product.corn", uniqueMessageHeader = true)
    CompletableFuture<PublishAck> sendCorn(Product corn);

    @JetStreamSubject(value = "product.apple")
    PublishAck sendApple(Product apple);

    PublishAck sendAnyProduct(@JetStreamSubject String productSubject, Product product);

    @JetStreamSubject(value = "map.products")
    PublishAck sendProductsMap(Map<String, List<Product>> customerProductsMap);
}
