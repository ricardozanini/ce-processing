package org.m88i.cloud.ce;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class CloudEventConsumerVerticleTest {

    private Vertx vertx;

    @Test
    public void assertSend(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new CloudEventConsumerVerticle(), context.asyncAssertSuccess());
        Async async = context.async();
        WebClient client = WebClient.create(vertx);
        client.post(CloudEventConsumerVerticle.DEFAULT_PORT, "localhost", "/")
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
