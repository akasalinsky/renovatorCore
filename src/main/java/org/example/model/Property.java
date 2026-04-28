package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Property {
    private List<Room> rooms = new ArrayList<>();
    private String propertyName;

    public Property(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    // Метод для получения общей площади (агрегация)
    public double getTotalArea() {
        return rooms.stream()
                .mapToDouble(Room::getFloorArea)
                .sum();
    }

    // Метод для получения площади всех стен под покраску/обои
    public double getTotalNetWallArea() {
        return rooms.stream()
                .mapToDouble(Room::getNetWallArea)
                .sum();
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room){
        rooms.add(room);
    }
}
