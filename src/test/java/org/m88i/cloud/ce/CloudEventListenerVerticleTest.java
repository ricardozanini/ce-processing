package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@RunWith(VertxUnitRunner.class)
public class CloudEventListenerVerticleTest {

    private Vertx vertx;

    @Test
    public void assertSend(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new CloudEventListenerVerticle(), context.asyncAssertSuccess());
        Async async = context.async();
        WebClient client = WebClient.create(vertx);
        client.get(CloudEventListenerVerticle.DEFAULT_PORT, "localhost", "/")
                .putHeader("content-type", "application/json")
                .putHeader("ce-specversion", "1.0")
                .putHeader("ce-source", "unit-test")
                .putHeader("ce-type", "test")
                .putHeader("ce-id", "123")
                .sendJsonObject(new JsonObject().put("name", "Dave"), response -> {
            context.assertTrue(response.succeeded());
            context.assertEquals(200, response.result().statusCode());
            async.complete();
        });
    }

    @After
    public void cleanUp(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }
}
