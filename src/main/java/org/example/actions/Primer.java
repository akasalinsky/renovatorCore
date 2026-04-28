package org.example.actions;

import org.example.model.Property;
import org.example.model.Room;

import java.util.stream.Collectors;

public class Primer {
    private static final double STANDARD_PRIMER_COVERAGE = 0.15;

    private double getLitersForRoom(Room room) {
        return room.getNetWallArea() * STANDARD_PRIMER_COVERAGE;
    }

    public double getResult(Room room){
        return getLitersForRoom(room);
    }

    public double getResult(Property property){
        return property.getRooms().stream()
                .mapToDouble(this::getLitersForRoom)
                .sum();
    }

    public String calculate(Room room) {
        double liters = Math.ceil(getLitersForRoom(room));

        return String.format("Комната: %s | Нужно литров грунтовки: %f (при расходе %.2fm). Расчетная площадь стен (чистая): %.2f м²",
                room.getRoomName(), liters, STANDARD_PRIMER_COVERAGE, room.getNetWallArea());
    }

    public String calculate(Property property) {
        String details = property.getRooms().stream()
                .map(this::calculate)
                .collect(Collectors.joining("\n"));

        double totalLiters = property.getRooms().stream()
                .mapToDouble(this::getLitersForRoom)
                .sum();

        return String.format("--- ОБЪЕКТ: %s ---\n--- Всего литров грунтовки: %s ---\n%s\n------------------",
                property.getPropertyName(), totalLiters, details);
    }
}
