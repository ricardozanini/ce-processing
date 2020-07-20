package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventImpl;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class ReactiveMessageProcessing {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessageProcessing.class);

    @Incoming("source")
    public void onMessage(CloudEvent<AttributesImpl, Map> message) {
        LOGGER.info("Received a CE from an internal, inmemory channel 'source' with ID: {}", message.getAttributes().getId());
    }

}
