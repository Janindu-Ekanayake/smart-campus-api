package com.janindu.smart.campus.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryLinks() {
        return Response.ok(Map.of(
                "version", "1.0",
                "status", "Active",
                "description", "Smart Campus Sensor & Room Management API",
                "contact", Map.of(
                        "name", "Janindu Ekanayake",
                        "module", "5COSC022W Client-Server Architectures",
                        "institution", "University of Westminster"
                ),
                "_links", Map.of(
                        "rooms", "/api/v1/rooms",
                        "sensors", "/api/v1/sensors"
                )
        )).build();
    }
}
