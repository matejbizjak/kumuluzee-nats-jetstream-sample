package si.matejbizjak.natsjetstream.sample.api.rest;

import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.Message;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import si.matejbizjak.natsjetstream.sample.api.entity.Demo;
import si.matejbizjak.natsjetstream.sample.api.subscriber.ComplexSubscriber;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Matej Bizjak
 */

@Path("/complex/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ComplexResource {

    @Inject
    private ComplexSubscriber complexSubscriber;
    @Inject
    @JetStreamProducer(connection = "secure")
    private JetStream jetStream;

    @GET
    @Path("/subject1")
    public Response getSimpleSub1() {
        if (jetStream == null) {
            return Response.serverError().build();
        }
        try {
            String uniqueID = UUID.randomUUID().toString();
            Headers headers = new Headers().add("Nats-Msg-Id", uniqueID);

            Demo entity = new Demo("john", 12.3, LocalDateTime.now(), 134, new Demo.InnerDemo("smith", 24.345f));

            Message message = NatsMessage.builder()
                    .subject("category.subject1")
                    .data(SerDes.serialize(entity))
                    .headers(headers)
                    .build();

            CompletableFuture<PublishAck> future = jetStream.publishAsync(message);
            PublishAck publishAck = future.get();
            return Response.ok(String.format("Message has been sent to stream %s", publishAck.getStream())).build();
        } catch (IOException | ExecutionException | InterruptedException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("/subject2")
    public Response getSimpleSub2() {
        if (jetStream == null) {
            return Response.serverError().build();
        }
        try {
            Demo entity = new Demo("michael", 12.3, LocalDateTime.now(), 134, new Demo.InnerDemo("brown", 24.345f));

            Message message = NatsMessage.builder()
                    .subject("category.subject2")
                    .data(SerDes.serialize(entity))
                    .build();

            PublishAck publishAck = jetStream.publish(message);
            return Response.ok(String.format("Message has been sent to stream %s", publishAck.getStream())).build();
        } catch (IOException | JetStreamApiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("/pull")
    public Response getPullSimple() {
        complexSubscriber.pullMsg();
        return Response.ok().build();
    }
}
