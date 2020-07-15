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
    public Publisher<String> ping() {
        return Flowable.interval(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.computation())
                .map(l -> String.format("{ \"counter\":  %s }", l));
    }

}
