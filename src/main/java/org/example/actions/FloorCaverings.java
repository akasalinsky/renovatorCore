package org.example.actions;

import org.example.model.Property;
import org.example.model.Room;

import java.util.stream.Collectors;

public class FloorCaverings {

    public double getResult(Room room){
        return Math.ceil(room.getNetWallArea());
    }

    public double getResult(Property property){
        return property.getRooms().stream()
                .mapToDouble(this::getResult)
                .sum();
    }

    public String calculate(Room room) {

        return String.format("Комната: %s | Нужно %f кв.м. напольного покрытия",
                room.getRoomName(), room.getFloorArea());
    }

    public String calculate(Property property) {
        String details = property.getRooms().stream()
                .map(this::calculate)
                .collect(Collectors.joining("\n"));

        return String.format("--- ОБЪЕКТ: %s ---\n%s\n------------------",
                property.getPropertyName(), details);
    }
}
