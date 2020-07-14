package org.m88i.cloud.ce;

public final class PublishAddress {
    // K_SINK is the environment variable injected by Knative Eventing
    private static final String PUBLISH_ADDRESS_ENV = "K_SINK";
    private static final String PUBLISH_ADDRESS_DEFAULT = "http://localhost:8080/";

    private PublishAddress() {

    }

    public static String getPublishAddress() {
        final String address = System.getenv(PUBLISH_ADDRESS_ENV);
        if (address == null || "".equals(address)) {
            return PUBLISH_ADDRESS_DEFAULT;
        }
        return address;
    }

}
