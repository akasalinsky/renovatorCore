package org.example.model;

public class Room {
    private String roomName;
    private double roomLength;
    private double roomWidth;
    private double roomHeight;
    private double openings; // в будущем надо заменить на объект, который содержит окна, двери и прочее

    public Room(String roomName, double roomLength, double roomWidth, double roomHeight, double openings) {
        this.roomName = roomName;
        this.roomLength = roomLength;
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
        this.openings = openings;
    }

    public String getRoomName() {
        return roomName;
    }

    public double getTotalWallArea(){
        return (roomLength * 2 + roomWidth * 2) * roomHeight;
    }

    public double getNetWallArea(){
        return  (roomLength * 2 + roomWidth * 2) * roomHeight - openings;
    }

    public double getFloorArea(){
        return roomLength * roomWidth;
    }

    public double getRoomLength() {
        return roomLength;
    }

    public double getRoomWidth() {
        return roomWidth;
    }

}
