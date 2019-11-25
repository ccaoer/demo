package com.demo.event;

/**
 * 事件对象
 */
public class DoorEvent {
    private Door door;

    public DoorEvent() {
    }

    public DoorEvent(Door door) {
        this.door = door;
    }

    public Door getDoor() {
        return door;
    }

    public void setDoor(Door door) {
        this.door = door;
    }
}
