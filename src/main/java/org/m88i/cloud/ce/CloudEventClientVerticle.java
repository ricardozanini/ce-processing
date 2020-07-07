package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class CloudEventClientVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventClientVerticle.class);

    public void start() {
        HttpClient client = vertx.createHttpClient();
        LOGGER.info("Creating new CE request");
        // TODO: URI must come from a config file, being injected by Knative
        HttpClientRequest request = client.postAbs("http://localhost:8080")
                .handler(httpClientResponse -> {
                    VertxMessageFactory
                            .createReader(httpClientResponse)
                            .onComplete(result -> {
                                if (result.succeeded()) {
                                    CloudEvent event = result.result().toEvent();
                                    LOGGER.info("Event is {}", event.toString());
                                }
                            });
                });

        CloudEvent event = CloudEventBuilder.v1()
                .withId("hello")
                .withType("example.vertx")
                .withSource(URI.create("http://localhost"))
                .build();

        // Write request as binary
        VertxMessageFactory
                .createWriter(request)
                .writeBinary(event);
    }
}
