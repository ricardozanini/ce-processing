package org.m88i.cloud.ce;

import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Simple HTTP server to listen to messages posted by success-ce-demo channel.
 */
public class CloudEventConsumerVerticle extends AbstractVerticle {
    // same port as defined in META-INF/microprofile-config.properties
    private static final int PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventConsumerVerticle.class);

    @Override
    public void start() throws Exception {
        vertx.createHttpServer()
                .requestHandler(req -> {
                            VertxCloudEvents.create().rxReadFromRequest(req)
                                    .subscribe((receivedEvent, throwable) -> {
                                        if (throwable != null) {
                                            LOGGER.error("Failed to process CE: {}", throwable.getMessage());
                                            req.bodyHandler(buffer -> {
                                               LOGGER.info("Received request: {} and headers: {}", new String(buffer.getBytes(), Charset.forName("UTF-8")), req.headers().toString());
                                            });
                                        }
                                        if (receivedEvent != null) {
                                            // I got a CloudEvent object:
                                            LOGGER.info("The event {}", Printer.beautify(receivedEvent));
                                        }
                                    });
                            req.response().end();
                        }
                )
                .rxListen(PORT)
                .subscribe(server -> {
                    LOGGER.info("Server running!");
                });
    }
}
