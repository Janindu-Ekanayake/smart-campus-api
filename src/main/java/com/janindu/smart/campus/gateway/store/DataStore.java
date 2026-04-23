package com.janindu.smart.campus.gateway.store;

import com.janindu.smart.campus.domain.entity.Room;
import com.janindu.smart.campus.domain.entity.Sensor;
import com.janindu.smart.campus.domain.entity.SensorReading;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataStore {
    private static final DataStore instance = new DataStore();

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    private DataStore() {
        rooms.put("R1", new Room("R1", "Main Lab", 50));
        sensors.put("S1", new Sensor("S1", "Temperature", "ACTIVE", 22.5, "R1"));
        sensorReadings.put("S1", new CopyOnWriteArrayList<>());
    }

    public static DataStore getInstance() { return instance; }

    public Map<String, Room> getRooms() { return rooms; }
    public Map<String, Sensor> getSensors() { return sensors; }
    public Map<String, List<SensorReading>> getSensorReadings() { return sensorReadings; }
}
