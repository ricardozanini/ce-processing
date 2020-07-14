package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;

import java.util.Map;
import java.util.Set;

public final class Printer {

    private Printer() {
    }

    public static String beautify(CloudEvent event) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n☁ ️cloudevents.Event\n")
                .append("Context Attributes,\n")
                .append("\tspecversion: ").append(event.getAttributes().getSpecversion()).append("\n")
                .append("\ttype: ").append(event.getAttributes().getType()).append("\n")
                .append("\tsource: ").append(event.getAttributes().getSource()).append("\n")
                .append("\tid: ").append(event.getAttributes().getId()).append("\n")
                // supressing extensions since looks like knative is injecting too much of them or the SDK is not handling that well.
                //.append("Extensions,").append(beautifyExtensions(event))
                .append("Data,\n\t").append(event.getData().orElse(""));
        return sb.toString();
    }

    private static String beautifyExtensions(CloudEvent event) {
        final Map<String, Object> extensions = event.getExtensions();
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : extensions.entrySet()) {
            sb.append("\t").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        if (extensions == null || extensions.isEmpty()) {
            sb.append("\n");
        }
        return sb.toString();
    }
}
