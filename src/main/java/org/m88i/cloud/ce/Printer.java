package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;

import java.util.Set;

public final class Printer {

    private Printer() {
    }

    public static String beautify(CloudEvent event) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n☁ ️cloudevents.Event\n")
                .append("Context Attributes,\n")
                .append("\tspecversion: ").append(event.getSpecVersion()).append("\n")
                .append("\ttype: ").append(event.getType()).append("\n")
                .append("\tsource: ").append(event.getSource()).append("\n")
                .append("\tid: ").append(event.getId()).append("\n")
                .append("\ttime: ").append(event.getTime()).append("\n")
                // supressing extensions since looks like knative is injecting too much of them or the SDK is not handling that well.
                //.append("Extensions,").append(beautifyExtensions(event))
                .append("Data,\n\t").append(new String(event.getData()));
        return sb.toString();
    }

    private static String beautifyExtensions(CloudEvent event) {
        final Set<String> extensions = event.getExtensionNames();
        final StringBuilder sb = new StringBuilder();
        for (String e : extensions) {
            sb.append("\t").append(e).append(": ").append(event.getExtension(e)).append("\n");
        }
        if (extensions == null || extensions.isEmpty()) {
            sb.append("\n");
        }
        return sb.toString();
    }
}
