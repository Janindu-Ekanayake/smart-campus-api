package com.janindu.smart.campus.domain.handler;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class GlobalSafetyNet implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) exception;
            int status = wae.getResponse().getStatus();
            String error = Response.Status.fromStatusCode(status) != null
                    ? Response.Status.fromStatusCode(status).getReasonPhrase()
                    : "Error";
            return Response.status(status)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("status", status, "error", error, "message", "No resource found at the requested path."))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("status", 500, "error", "Internal Server Error", "message", "An unexpected error occurred. Stack traces hidden for security."))
                .build();
    }
}
