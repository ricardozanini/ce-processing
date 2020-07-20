package org.m88i.cloud.ce;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class HTTPCloudEventResourceTest {

    @Test
    public void testListen() {
        given()
                .accept(ContentType.JSON)
                .body("{ \"message\": \"Hola Mundo!\" }")
                .header("content-type", MediaType.APPLICATION_JSON)
                .header("ce-specversion", "1.0")
                .header("ce-source", "/from/unit/test")
                .header("ce-type", "myevent")
                .header("ce-id", UUID.randomUUID().toString())
                .post("/")
                .then()
                .statusCode(204);
    }
}
