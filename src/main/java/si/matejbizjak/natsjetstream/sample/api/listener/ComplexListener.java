package si.matejbizjak.natsjetstream.sample.api.listener;

import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import si.matejbizjak.natsjetstream.sample.api.entity.Demo;

/**
 * @author Matej Bizjak
 */

public class ComplexListener {

    @JetStreamListener(connection = "secure", subject = "category.*")
//    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliver-policy", value = "new")})
    public void receive(Demo entity) {
        System.out.println(entity.getName());
    }
}
