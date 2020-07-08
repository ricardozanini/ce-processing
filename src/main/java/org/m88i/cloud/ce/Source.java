package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

public final class Source {
    private Source() {

    }

    public static final boolean isFromLocal(CloudEvent event)  {
        return event.getSource().equals(getLocal());
    }

    public static final URI getLocal() {
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            return URI.create(localHost.getHostName());
        } catch (UnknownHostException e) {
            return URI.create("http://localhost");
        }
    }
}
