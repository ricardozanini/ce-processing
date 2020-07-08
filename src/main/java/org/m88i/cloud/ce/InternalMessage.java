package org.m88i.cloud.ce;

import io.vertx.core.json.JsonObject;

public class InternalMessage {
    private InternalMessageType type;
    private String body;

    public InternalMessage(String body) {
        this.body = body;
        this.type = InternalMessageType.SUCCESS;
    }

    public InternalMessage(String body, InternalMessageType type) {
        this.body = body;
        this.type = type;
    }

    public InternalMessage(Throwable cause) {
        this.body = String.format("Failure: %s", cause.getMessage());
        this.type = InternalMessageType.FAILURE;
    }

    public static InternalMessage fromJson(JsonObject json) {
        return new InternalMessage(json.getString("body"), InternalMessageType.valueOf(json.getString("type")));
    }

    public InternalMessageType getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public JsonObject asJson() {
        return new JsonObject()
                .put("body", this.getBody())
                .put("type", this.getType());
    }

}
