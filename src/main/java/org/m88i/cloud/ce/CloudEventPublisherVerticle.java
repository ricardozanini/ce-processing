package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CloudEventPublisherVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventPublisherVerticle.class);
    private static final String PUBLISH_ADDRESS_ENV = "PUBLISHER_ADDRESS";
    private static final String PUBLISH_ADDRESS_DEFAULT = "http://localhost:8080/";

    public void start() {
        vertx.eventBus().consumer(Queues.CE_CLIENT_QUEUE, this::onMessage);
        LOGGER.info("CE Publisher started, will publish events to '{}' endpoint", getPublishAddress());
    }

    public void onMessage(Message<JsonObject> message) {
        final HttpClient client = vertx.createHttpClient();
        LOGGER.info("Received internal message. Creating new CE request");
        HttpClientRequest request = client.postAbs(getPublishAddress())
                .handler(httpClientResponse -> {
                    VertxMessageFactory
                            // we can't change to WebClient 'cause VertxMessageFactory.createReader doesn't have an HttpResponse input yet
                            .createReader(httpClientResponse)
                            .onComplete(result -> {
                                if (result.succeeded()) {
                                    CloudEvent event = result.result().toEvent();
                                    LOGGER.info("CloudEvent sent successfully! {}", event.toString());
                                } else {
                                    LOGGER.error("Failed to send CloudEvent: ", result.cause());
                                }
                            });
                });

        InternalMessage internalMsg = InternalMessage.fromJson(message.body());
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(internalMsg.getType().getCEType())
                .withData(internalMsg.getBody().getBytes())
                .withSource(Source.getLocal())
                .build();
        LOGGER.info("CE created {}, sending", event);
        // Write request as binary
        VertxMessageFactory
                .createWriter(request)
                .writeBinary(event);
    }

    private String getPublishAddress() {
        final String address = System.getenv(PUBLISH_ADDRESS_ENV);
        if (address == null || "".equals(address)) {
            return PUBLISH_ADDRESS_DEFAULT;
        }
        return address;
    }
}
