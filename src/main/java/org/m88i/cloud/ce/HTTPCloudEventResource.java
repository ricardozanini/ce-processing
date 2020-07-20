package org.m88i.cloud.ce;

import io.cloudevents.CloudEvent;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.http.Unmarshallers;
import org.jboss.resteasy.spi.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_JSON, HTTPCloudEventResource.STRUCTURED_TYPE})
public class HTTPCloudEventResource {

    public static final String STRUCTURED_TYPE = "application/cloudevents+json";
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPCloudEventResource.class);

    @POST
    public void listen(@Context HttpRequest request) {
        // we have to build the cloudevent manually since smallrye brings a very old SDK version (1.1.0)
        // we should upgrade to 2.x once we upgrade smallrye
        final MultivaluedMap<String, String> headers = request.getHttpHeaders().getRequestHeaders();
        if (request.getHttpHeaders().getMediaType().toString().contains(MediaType.APPLICATION_JSON)) {
            CloudEvent<AttributesImpl, Map> event =
                    Unmarshallers.binary(Map.class)
                            .withHeaders(() -> {
                                final Map<String, String> result = new HashMap<>();
                                headers.forEach((String k, List<String> v) -> {
                                    if (v != null && !v.isEmpty()) {
                                        if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(k)) {
                                            // we don't need to process the json data, treat as text
                                            result.put(k, v.get(0).split(";")[0]);
                                        } else {
                                            result.put(k, v.get(0));
                                        }
                                    }
                                });
                                return Collections.unmodifiableMap(result);
                            })
                            .withPayload(() -> {
                                try {
                                    return new String(request.getInputStream().readAllBytes());
                                } catch (IOException e) {
                                    LOGGER.error("Failed to process inputstream", e);
                                    throw new IllegalArgumentException(e);
                                }
                            })
                            .unmarshal();
            LOGGER.info("Processed cloudevent: {}", Printer.beautify(event));
        } else {
            throw new IllegalArgumentException("no cloudevent type identified");
        }
    }

}