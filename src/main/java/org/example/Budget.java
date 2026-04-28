package org.example;

import org.example.actions.Cellings;
import org.example.actions.FloorCaverings;
import org.example.actions.Primer;
import org.example.actions.Wallpaper;
import org.example.model.Property;
import org.example.model.Room;

import java.util.stream.Collectors;

public class Budget {
    private double wallpaperPrice;
    private double cellingPrice;
    private double primerPrice;
    private double floorCaveringPrice;

    private Wallpaper wallpaperCalculator = new Wallpaper();
    private Primer primerCalculator = new Primer();
    private FloorCaverings floorCalculator = new FloorCaverings();
    private Cellings cellingCalculator = new Cellings();

    public Budget(double wallpaperPrice, double cellingPrice, double primerPrice, double floorCaveringPrice) {
        this.wallpaperPrice = wallpaperPrice;
        this.cellingPrice = cellingPrice;
        this.primerPrice = primerPrice;
        this.floorCaveringPrice = floorCaveringPrice;
    }

    public double getResult(Room room){
        return (double) wallpaperCalculator.getResult(room)
                + primerCalculator.getResult(room)
                + floorCalculator.getResult(room)
                + cellingCalculator.getResult(room);
    }

    public double getResult(Property property) {
        return property.getRooms().stream()
                .mapToDouble(this::getResult)
                .sum();
    }

    public String calculate(Room room) {
        double total = getResult(room);
        return String.format("Бюджет комнаты %s: %.2f руб.", room.getRoomName(), total);
    }

    public String calculate(Property property) {
        String details = property.getRooms().stream()
                .map(this::calculate) // Вызывает calculate(Room) для каждой комнаты
                .collect(Collectors.joining("\n"));

        double grandTotal = getResult(property);

        return String.format("--- ДЕТАЛИЗАЦИЯ ОБЪЕКТА: %s ---\n%s\n--- ИТОГО К ОПЛАТЕ: %.2f ---",
                property.getPropertyName(), details, grandTotal);
    }
}
