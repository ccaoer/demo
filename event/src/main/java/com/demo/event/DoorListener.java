package com.demo.event;

/**
 * 监听器
 */
public interface DoorListener {
    public void doOpen(DoorEvent event);
    public void doClose(DoorEvent event);
}
