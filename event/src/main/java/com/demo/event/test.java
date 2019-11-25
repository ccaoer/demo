package com.demo.event;

public class test {
    public static void main(String[] args) {
        Door door = new Door();
        door.addDoorListener(new DoorListener() {
            public void doOpen(DoorEvent event) {
                System.out.println(event + " bomb~~~~~");
            }

            public void doClose(DoorEvent event) {
                System.out.println(event + " bomb2~~~~~~");
            }
        });
        door.open();
        door.close();
    }
}
