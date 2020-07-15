package org.m88i.cloud.ce;


import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.se.SeContainerInitializer;

@ApplicationScoped
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new CloudEventConsumerVerticle(), handler -> {
            if (handler.succeeded()) {
                LOGGER.info("Vertx successfully deployed");
                SeContainerInitializer.newInstance().initialize();
                LOGGER.info("Container initialized");
            } else {
                LOGGER.error("Impossible to deploy verticle", handler.cause());
            }
        });
    }
}