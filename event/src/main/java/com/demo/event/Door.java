package com.demo.event;

/**
 * 事件源
 */
public class Door {
    private DoorListener listener;

    public void addDoorListener(DoorListener listener) {
        this.listener = listener;
    }

    public void open() {
        if(listener != null) {
            listener.doOpen(new DoorEvent());
        }
        System.out.println("door is opened...");
    }

    public void close() {
        if(listener != null) {
            listener.doClose(new DoorEvent());
        }
        System.out.println("door is closed...");
    }
}