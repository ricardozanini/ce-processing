package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;
import io.cloudevents.http.vertx.VertxMessageFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudEventListenerVerticle extends AbstractVerticle {
    static final int DEFAULT_PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventListenerVerticle.class);
    private static final String CLOUDEVENTS_CONTENT_TYPE = "application/cloudevents+json";
    private static final int SERVER_ERROR = 500;

    public void start() {
        LOGGER.info("Initializing Vertx Server");
        vertx.createHttpServer()
                .requestHandler(req -> {
                    VertxMessageFactory.createReader(req)
                            .onSuccess(result -> {
                                CloudEvent event = result.toEvent();
                                // Echo the message, as structured mode
                                VertxMessageFactory
                                        .createWriter(req.response())
                                        .writeStructured(event, CLOUDEVENTS_CONTENT_TYPE);
                                LOGGER.info("Received event: {}", event.toString());
                                // we now send a message to our internal bus notifying the publisher that we processed a CE successfully
                                LOGGER.info("Sending internal event to emit a success CloudEvent to the platform");
                                emitInternalEvent(new InternalMessage(String.format("CE Processed ID: %s", event.getId())));
                            })
                            .onFailure(result -> {
                                LOGGER.error("Failed to process the request {}", result.getCause());
                                req.response().setStatusCode(SERVER_ERROR).end();
                                // ops, an error happened, let's notify our publisher
                                LOGGER.info("Sending internal event to emit a failure CloudEvent to the platform");
                                emitInternalEvent(new InternalMessage(result.getCause()));
                            });
                })
                .listen(DEFAULT_PORT, serverResult -> {
                    if (serverResult.succeeded()) {
                        LOGGER.info("Server started on port {}", serverResult.result().actualPort());
                    } else {
                        LOGGER.error("Error starting the server", serverResult.cause());
                    }
                });
    }

    private void emitInternalEvent(InternalMessage message) {
        vertx.eventBus().publish(Queues.CE_CLIENT_QUEUE, message.asJson());
    }
}
