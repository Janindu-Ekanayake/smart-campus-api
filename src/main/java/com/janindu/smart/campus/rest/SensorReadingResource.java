package com.janindu.smart.campus.rest;

import com.janindu.smart.campus.domain.fault.SensorUnavailableException;
import com.janindu.smart.campus.domain.entity.Sensor;
import com.janindu.smart.campus.domain.entity.SensorReading;
import com.janindu.smart.campus.gateway.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        if (!store.getSensors().containsKey(sensorId)) {
            return Response.status(404)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("status", 404, "error", "Not Found", "message", "Sensor not found: " + sensorId))
                    .build();
        }
        List<SensorReading> readings = store.getSensorReadings().getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(404)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("status", 404, "error", "Not Found", "message", "Sensor not found: " + sensorId))
                    .build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Cannot add reading: Sensor " + sensorId + " is in MAINTENANCE and cannot accept new readings.");
        }

        // Auto-generate id and timestamp if not provided
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Side effect: update parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        store.getSensorReadings().putIfAbsent(sensorId, new CopyOnWriteArrayList<>());
        store.getSensorReadings().get(sensorId).add(reading);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
