package si.matejbizjak.natsjetstream.sample.api.rest;

import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamProducer;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.Message;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.NatsMessage;
import si.matejbizjak.natsjetstream.sample.api.subscriber.SimpleSubscriber;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Matej Bizjak
 */

@Path("/simple/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class SimpleResource {

    @Inject
    private SimpleSubscriber simpleSubscriber;

    @Inject
    @JetStreamProducer(context = "context1")
    private JetStream jetStream;

    @GET
    @Path("/subject1")
    public Response getSub1() {
        try {
//            Headers headers = new Headers().add("Nats-Msg-Id", "1");  // TODO kako dobiti unique id?
            Message message = NatsMessage.builder()
                    .subject("subject1")
                    .data(SerDes.serialize("test message"))
//                    .headers(headers)
                    .build();

            PublishAck publishAck = jetStream.publish(message);
            return Response.ok(String.format("Message has been sent to stream %s", publishAck.getStream())).build();
        } catch (IOException | JetStreamApiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("/subject2")
    public Response getSub2() {
        try {
            Message message = NatsMessage.builder()
                    .subject("subject2")
                    .data(SerDes.serialize("test message to pull manually"))
                    .build();

            PublishAck publishAck = jetStream.publish(message);
            return Response.ok(String.format("Message has been sent to stream %s", publishAck.getStream())).build();
        } catch (IOException | JetStreamApiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @GET
    @Path("/pull")
    public Response getPull() {
        simpleSubscriber.pullMsg();
        return Response.ok().build();
    }
}
