package org.example.actions;

import org.example.model.Property;
import org.example.model.Room;

import java.util.stream.Collectors;

public class Wallpaper {

    private static final double STANDARD_STRIP_WIDTH = 1.06;

    public int getResult(Room room){
        return (int) Math.ceil(room.getNetWallArea()/ (STANDARD_STRIP_WIDTH * room.getRoomLength()));
    }

    public int getResult(Property property){
        return property.getRooms().stream()
                .mapToInt(this::getResult)
                .sum();
    }

    public String calculate(Room room) {
        int strips = (int) Math.ceil(room.getNetWallArea()/ (STANDARD_STRIP_WIDTH * room.getRoomLength()));

        return String.format("Комната: %s | Нужно полос: %d (при ширине %.2fm). Расчетная площадь стен (чистая): %.2f м²",
                room.getRoomName(), strips, STANDARD_STRIP_WIDTH, room.getNetWallArea());
    }

    public String calculate(Property property) {
        String details = property.getRooms().stream()
                .map(this::calculate)
                .collect(Collectors.joining("\n"));

        return String.format("--- ОБЪЕКТ: %s ---\n%s\n------------------",
                property.getPropertyName(), details);
    }
}
