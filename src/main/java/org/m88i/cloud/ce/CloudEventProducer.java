package org.m88i.cloud.ce;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.format.Wire;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import io.cloudevents.v1.http.Marshallers;
import io.smallrye.reactive.messaging.cloudevents.CloudEventMessage;
import io.smallrye.reactive.messaging.http.HttpMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class CloudEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventProducer.class);

    @Incoming("source")
    @Outgoing("success-ce-demo")
    public HttpMessage<String> onMessage(String message) throws URISyntaxException, JsonProcessingException {
        LOGGER.info("Received internal message. Creating new CE request with message '{}'", message);

        final CloudEventImpl<String> ce = CloudEventBuilder.<String>builder()
                .withId(UUID.randomUUID().toString())
                .withType(InternalMessageType.SUCCESS.getCEType())
                .withData(message)
                .withTime(ZonedDateTime.now())
                .withSubject("New Counter")
                .withDataContentType("application/json")
                .withSource(new URI("/smallrye/cloudEventMessage"))
                .build();

        final Wire<String, String, String> wire = Marshallers.<String>binary().withEvent(() -> ce).marshal();
        final Map<String, List<String>> headers = new HashMap<>();
        wire.getHeaders().forEach((k, v) -> {
            headers.put(k, Collections.singletonList(v));
        });

        final HttpMessage<String> httpMessage = HttpMessage.HttpMessageBuilder.<String>create()
                .withMethod("POST")
                .withPayload(wire.getPayload().orElse(""))
                .withHeaders(headers)
                .build();

        return httpMessage;
    }
}
