package com.janindu.smart.campus.domain.handler;

import com.janindu.smart.campus.domain.fault.LinkedNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class LinkedNotFoundHandler implements ExceptionMapper<LinkedNotFoundException> {
    @Override
    public Response toResponse(LinkedNotFoundException exception) {
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("status", 422, "error", "Unprocessable Entity", "message", exception.getMessage()))
                .build();
    }
}
