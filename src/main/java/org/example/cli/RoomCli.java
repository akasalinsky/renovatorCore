package org.example.cli;

import org.example.i18n.MessageProvider;
import org.example.model.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoomCli {
    private Room currentRoom;
    private MessageProvider messageProvider;
    private final Locale locale;
    private static final Map<String, String> COMMAND_ALIASES = Map.of(
            "создать", "create",
            "описание", "describe",
            "план", "plan",
            "площадь", "area",
            "обои", "wallpaper",
            "выход", "exit",
            "помощь", "help",
            "язык", "lang",
            "проем", "opening"
    );

    public RoomCli() {
        this(Locale.forLanguageTag("ru"));
    }

    public RoomCli(Locale locale) {
        this.locale = locale;
        this.messageProvider = new MessageProvider(locale);
    }

    public String execute(String command) {
        if (command == null) {
            return messageProvider.get("empty.message");
        }
        String trimmed = command.trim();
        if (trimmed.isEmpty()) {
            return messageProvider.get("empty.message");
        }

        String[] parts = trimmed.split("\\s+");
        String cmd = parts[0].toLowerCase(Locale.ROOT);
        //String cmd = parts[0].toLowerCase();
        cmd = COMMAND_ALIASES.getOrDefault(cmd, cmd);

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
                return messageProvider.get("goodbye");
            case "opening":
                return handleOpening(parts);
            default:
                return messageProvider.get("unknown.command", trimmed);
        }
    }

    private String handleCreate(String[] parts) {
        if (parts.length != 5) {
            return messageProvider.get("create.usage");
        }
        try {
            String name = parts[1];
            int length = Integer.parseInt(parts[2]);
            int width = Integer.parseInt(parts[3]);
            int height = Integer.parseInt(parts[4]);

            boolean exists = currentRoom != null && currentRoom.getName().equals(name);
            currentRoom = new Room(name, height, List.of(new Wall(length, height, 0), new Wall(width, height, 1), new Wall(length, height, 2), new Wall(width, height, 3)), List.of(90, 90, 90, 90));
            return messageProvider.get("create.success", name);

        } catch (NumberFormatException e) {
            return messageProvider.get("create.usage");
        }
    }

    private String handleDescribe() {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }
        return String.format(Locale.US,messageProvider.get("room.description", currentRoom.getName(), currentRoom.getWalls().size(), currentRoom.getHeight(), currentRoom.totalOpeningsArea()));

    }

    private String handlePlan(String[] parts) {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }
        int scale = 1000;
        if (parts.length > 1) {
            try {
                scale = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return messageProvider.get("plan.usage");
            }
        }
        String plan = currentRoom.renderPlanWithNumbers(scale);
        return "<pre>" + plan + "</pre>";
    }

    private String handleArea() {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }
        double area = currentRoom.getFloorArea() / 1_000_000.0;
        String formatted = String.format(Locale.US, "%.2f", area);
        return messageProvider.get("floor.area", formatted);
    }

    private String handleWallpaper(String[] parts) {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }
        if (parts.length == 1 ) {
            try {
                int rolls = currentRoom.getWallpaperRolls();
                return messageProvider.get("wallpaper.rolls", rolls);
            } catch (NumberFormatException e) {
                return messageProvider.get("wallpaper.usage");
            }
        }
        if (parts.length == 3 ) {
            try {
                int rollWidth = Integer.parseInt(parts[1]);
                int rollLength = Integer.parseInt(parts[2]);
                int rolls = currentRoom.getWallpaperRolls(rollWidth, rollLength);
                return messageProvider.get("wallpaper.rolls", rolls);
            } catch (NumberFormatException e) {
                return messageProvider.get("wallpaper.usage");
            }
        }
            return messageProvider.get("wallpaper.usage");
    }

    private String handleOpening(String[] parts) {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }

        if (parts.length == 7) {
            try {
                int wallNumber = Integer.parseInt(parts[1]);
                int width = Integer.parseInt(parts[3]);
                int height = Integer.parseInt(parts[4]);
                int distanceFromLeft = Integer.parseInt(parts[5]);
                int distanceFromFloor = Integer.parseInt(parts[6]);

           /* if(OpeningName.equalsIgnoreCase("окно") || OpeningName.equalsIgnoreCase("windows")){openingType = OpeningType.WINDOW;}
            if(OpeningName.equalsIgnoreCase("дверь") || OpeningName.equalsIgnoreCase("door")){openingType = OpeningType.DOOR;}
            else {throw new IllegalArgumentException(messageProvider.get("opening.usage"));}
*/
                WallOpening wallOpening = new WallOpening(new Opening(OpeningType.WINDOW, width, height), distanceFromLeft, distanceFromFloor);

                currentRoom = currentRoom.withOpening(wallNumber, wallOpening);
                return messageProvider.get("opening.success");

            } catch (NumberFormatException e) {
                return messageProvider.get("opening.usage");
            }

        }
        if (parts.length == 6) {
            try {
                int wallNumber = Integer.parseInt(parts[1]);
                int width = Integer.parseInt(parts[3]);
                int height = Integer.parseInt(parts[4]);
                int distanceFromLeft = Integer.parseInt(parts[5]);

                WallOpening wallOpening = new WallOpening(new Opening(OpeningType.DOOR, width, height), distanceFromLeft, 0);

                currentRoom = currentRoom.withOpening(wallNumber, wallOpening);
                return messageProvider.get("opening.success");

            } catch (NumberFormatException e) {
                return messageProvider.get("opening.usage");
            }

        }
        return messageProvider.get("opening.usage");
    }
}
