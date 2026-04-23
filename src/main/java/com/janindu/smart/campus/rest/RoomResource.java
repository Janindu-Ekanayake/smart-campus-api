package com.janindu.smart.campus.rest;

import com.janindu.smart.campus.domain.fault.RoomNotEmptyException;
import com.janindu.smart.campus.domain.entity.Room;
import com.janindu.smart.campus.gateway.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        return Response.ok(new ArrayList<>(store.getRooms().values())).build();
    }

    @GET
    @Path("/{id}")
    public Response getRoom(@PathParam("id") String id) {
        Room room = store.getRooms().get(id);
        if (room == null) {
            return Response.status(404)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("status", 404, "error", "Not Found", "message", "Room not found: " + id))
                    .build();
        }
        return Response.ok(room).build();
    }

    @POST
    public Response createRoom(Room room) {
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }
        store.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        Room room = store.getRooms().get(id);
        if (room == null) {
            return Response.status(404)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("status", 404, "error", "Not Found", "message", "Room not found: " + id))
                    .build();
        }
        boolean containsSensors = store.getSensors().values().stream()
                .anyMatch(s -> id.equals(s.getRoomId()));
        if (containsSensors) {
            throw new RoomNotEmptyException("Room " + id + " cannot be deleted because it still contains sensors.");
        }
        store.getRooms().remove(id);
        return Response.noContent().build();
    }
}
