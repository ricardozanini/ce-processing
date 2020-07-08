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

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.UUID;

public class CloudEventPublisherVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventPublisherVerticle.class);

    public void start() {
        vertx.eventBus().consumer(Queues.CE_CLIENT_QUEUE, this::onMessage);
        LOGGER.info("CE Publisher started");
    }

    public void onMessage(Message<JsonObject> message) {
        HttpClient client = vertx.createHttpClient();
        LOGGER.info("Received internal message. Creating new CE request");
        /* TODO: URI must come from a config file, being injected by Knative
           The AbstractVerticle#config() method allows accessing the verticle configuration that has been provided.
           The second parameter is a default value in case no specific value was given.
           see: https://vertx.io/docs/guide-for-java-devs/#_the_http_server_verticle
         */
        HttpClientRequest request = client.postAbs("http://localhost:8180")
                .handler(httpClientResponse -> {
                    VertxMessageFactory
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
                .withSource(getSource())
                .build();

        // Write request as binary
        VertxMessageFactory
                .createWriter(request)
                .writeBinary(event);
    }

    private URI getSource() {
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            return URI.create(localHost.getHostName());
        } catch (UnknownHostException e) {
           return URI.create("http://localhost");
        }
    }
}
