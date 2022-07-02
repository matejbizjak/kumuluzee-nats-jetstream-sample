package si.matejbizjak.natsjetstream.sample.api.listener;

import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import si.matejbizjak.natsjetstream.sample.api.entity.Demo;

/**
 * @author Matej Bizjak
 */

public class ComplexListener {

    @JetStreamListener(connection = "secure", subject = "category.*")
//    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliver-policy", value = "new")})
    public void receive(Demo entity/*, JetStreamMessage msg*/) {
//        try {
//            Thread.sleep(3000);
//            msg.inProgress();
//            Thread.sleep(3000);
//            msg.inProgress();
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println(entity.getName());
    }
}
