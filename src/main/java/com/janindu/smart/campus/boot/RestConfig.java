package com.janindu.smart.campus.boot;

import com.janindu.smart.campus.domain.handler.GlobalSafetyNet;
import com.janindu.smart.campus.domain.handler.LinkedNotFoundHandler;
import com.janindu.smart.campus.domain.handler.NotFoundHandler;
import com.janindu.smart.campus.domain.handler.RoomNotEmptyHandler;
import com.janindu.smart.campus.domain.handler.SensorUnavailableHandler;
import com.janindu.smart.campus.gateway.http.LoggingFilter;
import com.janindu.smart.campus.rest.DiscoveryResource;
import com.janindu.smart.campus.rest.RoomResource;
import com.janindu.smart.campus.rest.SensorReadingResource;
import com.janindu.smart.campus.rest.SensorResource;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.util.HashMap;
import java.util.Map;

// DO NOT CHANGE THIS PATH! The coursework rubric explicitly requires "/api/v1" for maximum marks in Task 1.1.
@ApplicationPath("/api/v1")
public class RestConfig extends ResourceConfig {

    public RestConfig() {
        // Register all resources explicitly
        register(DiscoveryResource.class);
        register(RoomResource.class);
        register(SensorResource.class);
        register(SensorReadingResource.class);

        // Register all providers explicitly
        register(NotFoundHandler.class);
        register(LinkedNotFoundHandler.class);
        register(RoomNotEmptyHandler.class);
        register(SensorUnavailableHandler.class);
        register(GlobalSafetyNet.class);
        register(LoggingFilter.class);

        // Disable Jersey's MappableExceptionWrapperInterceptor
        // This interceptor rewraps 404/405 as 500 before mappers can handle them
        Map<String, Object> properties = new HashMap<>();
        properties.put("jersey.config.server.exception.wrapInProcessingErrors", false);
        addProperties(properties);
    }
}
