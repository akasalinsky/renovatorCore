package org.example.model;

import java.util.List;
import java.util.Locale;

public class RoomCli {
    private Room currentRoom;

    public String execute(String command) {
        if (command == null) {
            return "Unknown command: ";
        }
        String trimmed = command.trim();
        if (trimmed.isEmpty()) {
            return "Unknown command: ";
        }

        String[] parts = trimmed.split("\\s+");
        String cmd = parts[0].toLowerCase(Locale.ROOT);

        switch (cmd) {
            case "create":
                return handleCreate(parts);
            case "describe":
                return handleDescribe();
            case "plan":
                return handlePlan(parts);
            case "area":
                return handleArea();
            case "wallpaper":
                return handleWallpaper(parts);
            case "exit":
                return "Goodbye!";
            default:
                return "Unknown command: " + trimmed;
        }
    }

    private String handleCreate(String[] parts) {
        if (parts.length != 5) {
            return "Usage: create <name> <length> <width> <height>";
        }
        try {
            String name = parts[1];
            int length = Integer.parseInt(parts[2]);
            int width = Integer.parseInt(parts[3]);
            int height = Integer.parseInt(parts[4]);

            boolean exists = currentRoom != null && currentRoom.getName().equals(name);
            currentRoom = new Room(name, height, List.of(new Wall(length), new Wall(width), new Wall(length), new Wall(width)), List.of(90, 90, 90, 90));
            return "Room '" + name + "' created.";

        } catch (NumberFormatException e) {
            return "Usage: create <name> <length> <width> <height>";
        }
    }

    private String handleDescribe() {
        if (currentRoom == null) {
            return "No room created.";
        }
        return currentRoom.describe();
    }

    private String handlePlan(String[] parts) {
        if (currentRoom == null) {
            return "No room created.";
        }
        int scale = 1000;
        if (parts.length > 1) {
            try {
                scale = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return "Usage: plan [scale]";
            }
        }
        return currentRoom.renderPlan(scale);
    }

    private String handleArea() {
        if (currentRoom == null) {
            return "No room created.";
        }
        double area = currentRoom.getFloorArea();
        return String.format(Locale.US, "Floor area: %.2f m²", area/1000000);
    }

    private String handleWallpaper(String[] parts) {
        if (currentRoom == null) {
            return "No room created.";
        }
        if (parts.length != 3) {
            return "Usage: wallpaper <rollWidth> <rollLength>";
        }
        try {
            int rollWidth = Integer.parseInt(parts[1]);
            int rollLength = Integer.parseInt(parts[2]);
            int rolls = currentRoom.getWallpaperRolls(rollWidth, rollLength);
            return "You need " + rolls + " rolls.";
        } catch (NumberFormatException e) {
            return "Usage: wallpaper <rollWidth> <rollLength>";
        }
    }
}
