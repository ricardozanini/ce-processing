package org.m88i.cloud.ce;

public enum InternalMessageType {
    SUCCESS,
    FAILURE;

    public String getCEType() {
        return String.format("%s.ce.demo", this.name().toLowerCase());
    }
}
