package org.m88i.cloud.ce;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.smallrye.reactive.messaging.cloudevents.CloudEventMessage;
import io.smallrye.reactive.messaging.cloudevents.CloudEventMessageBuilder;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class PingSource {
    @Outgoing("source")
    public Publisher<CloudEventMessage<String>> ping() {
        return Flowable.interval(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.computation())
                .map(l -> new CloudEventMessageBuilder<String>()
                        .withId(UUID.randomUUID().toString())
                        .withType("counter")
                        .withSource(new URI("local://pingsource"))
                        .withDataContentType("text/plain")
                        .withTime(ZonedDateTime.now())
                        .withData(Long.toString(l))
                        .build());
    }

}
