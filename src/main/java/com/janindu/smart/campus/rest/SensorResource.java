package com.janindu.smart.campus.rest;

import com.janindu.smart.campus.domain.fault.LinkedNotFoundException;
import com.janindu.smart.campus.domain.entity.Room;
import com.janindu.smart.campus.domain.entity.Sensor;
import com.janindu.smart.campus.gateway.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>(store.getSensors().values());
        if (type != null && !type.isEmpty()) {
            result = result.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        return Response.ok(result).build();
    }

    @POST
    public Response createSensor(Sensor sensor) {
        Room room = store.getRooms().get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedNotFoundException("Cannot link: Room ID " + sensor.getRoomId() + " does not exist.");
        }
        store.getSensors().put(sensor.getId(), sensor);
        store.getSensorReadings().putIfAbsent(sensor.getId(), new CopyOnWriteArrayList<>());

        // Keep room's sensorIds in sync
        if (!room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // SUB-RESOURCE LOCATOR
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
