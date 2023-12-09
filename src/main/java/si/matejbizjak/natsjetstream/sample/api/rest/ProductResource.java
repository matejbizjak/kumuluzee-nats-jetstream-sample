package si.matejbizjak.natsjetstream.sample.api.rest;

import com.kumuluz.ee.nats.jetstream.annotations.JetStreamClient;
import io.nats.client.api.PublishAck;
import si.matejbizjak.natsjetstream.sample.api.client.ProductClient;
import si.matejbizjak.natsjetstream.sample.api.dto.Product;
import si.matejbizjak.natsjetstream.sample.api.subscriber.ProductSubscriber;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Matej Bizjak
 */

@Path("/product/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ProductResource {

    @Inject
    private ProductSubscriber productSubscriber;

    @Inject
    @JetStreamClient
    private ProductClient productClient;

    @POST
    @Path("/corn")
    public Response postCorn(Product corn) {
        try {
            CompletableFuture<PublishAck> future = productClient.sendCorn(corn);
            PublishAck publishAck = future.get();
            return Response.ok(String.format("Message has been sent to stream %s", publishAck.getStream())).build();
        } catch (ExecutionException | InterruptedException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @POST
    @Path("/apple")
    public Response postApple(Product apple) {
        PublishAck publishAck = productClient.sendApple(apple);
        return Response.ok(String.format("Message has been sent to stream %s", publishAck.getStream())).build();
    }

    @POST
    @Path("/any-product")
    public Response postAnyProduct(Product product) {
        String topic = "product." + product.getName().toLowerCase();
        PublishAck publishAck = productClient.sendAnyProduct(topic, product);
        return Response.ok(String.format("Message has been sent to stream %s", publishAck.getStream())).build();
    }

    @POST
    @Path("/productsMap")
    public Response postProductsMap() {
        Map<String, List<Product>> customerProductsMap = new HashMap<>();

        List<Product> products1 = new ArrayList<>();
        products1.add(new Product(1, "name1", "", null, 1, null, Instant.now()));
        products1.add(new Product(2, "name2", "", null, 2, null, Instant.now()));
        customerProductsMap.put("1", products1);

        List<Product> products2 = new ArrayList<>();
        products2.add(new Product(3, "name3", "", null, 3, null, Instant.now()));
        products2.add(new Product(4, "name4", "", null, 4, null, Instant.now()));
        customerProductsMap.put("2", products2);

        PublishAck publishAck = productClient.sendProductsMap(customerProductsMap);
        return Response.ok(String.format("Message has been sent to stream %s", publishAck.getStream())).build();
    }

    @GET
    @Path("/pullCorn")
    public Response pullCorn() {
        productSubscriber.pullCorn();
        return Response.ok().build();
    }
}
