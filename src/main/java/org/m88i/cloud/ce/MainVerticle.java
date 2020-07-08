package org.m88i.cloud.ce;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Promise<Void> promise) throws Exception {
        Promise<String> listenerDeployment = Promise.promise();
        vertx.deployVerticle(new CloudEventListenerVerticle(), listenerDeployment);

        listenerDeployment.future().compose(id -> {
            Promise<String> publisherDeployment = Promise.promise();
            vertx.deployVerticle(new CloudEventPublisherVerticle(), publisherDeployment);
            return publisherDeployment.future();
        }).onComplete(ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Successfully deployed MainVerticle");
                promise.complete();
            } else {
                promise.fail(ar.cause());
            }
        });
    }
}
