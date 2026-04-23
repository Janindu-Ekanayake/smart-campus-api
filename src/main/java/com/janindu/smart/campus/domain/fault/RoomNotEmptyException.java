package com.janindu.smart.campus.domain.fault;

public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) { super(message); }
}
