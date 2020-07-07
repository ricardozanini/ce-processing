package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudEventServerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventServerVerticle.class);

    public void start() {
        LOGGER.info("Initializing Vertx Server");
        vertx.createHttpServer()
                .requestHandler(req -> {
                    VertxMessageFactory.createReader(req)
                            .onComplete(result -> {
                                // If decoding succeeded, we should write the event back
                                if (result.succeeded()) {
                                    CloudEvent event = result.result().toEvent();
                                    // Echo the message, as structured mode
                                    VertxMessageFactory
                                            .createWriter(req.response())
                                            .writeStructured(event, "application/cloudevents+json");
                                }
                                req.response().setStatusCode(500).end();
                            });
                })
                .listen(8080, serverResult -> {
                    if (serverResult.succeeded()) {
                        LOGGER.info("Server started on port {}", serverResult.result().actualPort());
                    } else {
                        LOGGER.error("Error starting the server", serverResult.cause());
                    }
                });
    }
}
