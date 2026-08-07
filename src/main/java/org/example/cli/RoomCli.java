package org.example.cli;

import org.example.i18n.MessageProvider;
import org.example.model.*;
import org.example.service.FloorType;
import org.example.service.RoomBudgetService;
import org.example.service.RoomCalculationService;
import org.example.service.RoomRenderingService;
import org.example.service.money.PriceCatalog;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoomCli {
    private Room currentRoom;
    private MessageProvider messageProvider;
    private final Locale locale;
    private RoomRenderingService roomRenderingService;
    private RoomCalculationService roomCalculationService;
    private RoomBudgetService budgetService;

    private static final Map<String, String> COMMAND_ALIASES = Map.of(
            "создать", "create",
            "описание", "describe",
            "план", "plan",
            "площадь", "area",
            "обои", "wallpaper",
            "выход", "exit",
            "помощь", "help",
            "язык", "lang",
            "проем", "opening",
            "смета", "budget"
    );

    public RoomCli() {
        this(Locale.forLanguageTag("ru"));
    }

    public RoomCli(Locale locale) {
        this.locale = locale;
        this.messageProvider = new MessageProvider(locale);
        this.roomCalculationService = new RoomCalculationService();
        this.roomRenderingService = new RoomRenderingService();
        this.budgetService = new RoomBudgetService(PriceCatalog.defaultRub());

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
        cmd = COMMAND_ALIASES.getOrDefault(cmd, cmd);

        switch (cmd) {
            case "create":
                return handleCreate(parts);
            case "describe":
                return handleDescribe();
            case "plan":
                return handlePlanImage(parts);
            case "area":
                return handleArea();
            case "wallpaper":
                return handleWallpaper(parts);
            case "exit":
                return messageProvider.get("goodbye");
            case "opening":
                return handleOpening(parts);
            case "budget":
                return handleBudget(parts);
            default:
                return messageProvider.get("unknown.command", trimmed);
        }
    }

    private String handleCreate(String[] parts) {
        if (parts.length < 5 || parts.length > 9) {
            return messageProvider.get("create.usage");
        }
        try {
            String name = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length - 3));;

            int length = Integer.parseInt(parts[parts.length - 3]);
            int width = Integer.parseInt(parts[parts.length - 2]);
            int height = Integer.parseInt(parts[parts.length - 1]);

            if (name.isEmpty()) {
                return messageProvider.get("create.usage");
            }

            boolean exists = currentRoom != null && currentRoom.name().equals(name);
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
        return String.format(Locale.US,messageProvider.get("room.description", currentRoom.name(),
                currentRoom.walls().size(), currentRoom.height(),
                (roomCalculationService.getTotalOpeningsArea(currentRoom)/1000000)));

    }

    private String handlePlan(String[] parts) {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }
        int scale = 1000;
        if (parts.length > 1) {
            try {
                scale = Integer.parseInt(parts[1]);
                if( scale <= 0){
                    throw new IllegalArgumentException("scale must be a positive integer");
                }
            } catch (NumberFormatException e) {
                return messageProvider.get("plan.usage");
            }
        }
        String plan = roomRenderingService.renderPlanWithNumbers(currentRoom, scale);
        return "<pre>" + plan + "</pre>";
    }

    private String handlePlanImage(String[] parts) {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }
        int scale = 50;
        if (parts.length > 1 && parts.length < 10) {
            try {
                scale = Integer.parseInt(parts[1]);
                if (scale <= 0) {
                    //throw new IllegalArgumentException("scale must be positive");
                    return messageProvider.get("plan.usage");
                }
            } catch (NumberFormatException e) {
                return messageProvider.get("plan.usage");
            }
        }
        if (scale < 100) {
            try {
                byte[] imageBytes = roomRenderingService.renderPlanImage(currentRoom, scale);
                return "IMAGE:" + java.util.Base64.getEncoder().encodeToString(imageBytes);
            }
            catch (IOException e) {
                //throw new RuntimeException("Ошибка генерации плана: " + e.getMessage(), e);
                return messageProvider.get("plan.usage");
            }
        } else {
            String plan = roomRenderingService.renderPlanWithNumbers(currentRoom, scale);
            return "<pre>" + plan + "</pre>";
        }
    }

    private String handleArea() {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }
        double area = roomCalculationService.getFloorArea(currentRoom) / 1_000_000.0;
        String formatted = String.format(Locale.US, "%.2f", area);
        return messageProvider.get("floor.area", formatted);
    }

    private String handleWallpaper(String[] parts) {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }
        if (parts.length < 1 || parts.length > 4) {
            return messageProvider.get("wallpaper.usage");
        }

        if (parts.length == 1 ) {
            try {
                int rolls = roomCalculationService.calculateWallpaperRolls(currentRoom);
                return messageProvider.get("wallpaper.rolls", rolls);
            } catch (NumberFormatException e) {
                return messageProvider.get("wallpaper.usage");
            }
        }
        if (parts.length == 3 ) {
            try {
                int rollWidth = Integer.parseInt(parts[1]);
                int rollLength = Integer.parseInt(parts[2]);
                int rolls = roomCalculationService.calculateWallpaperRolls(currentRoom, rollWidth, rollLength);
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

        if (parts.length < 1 || parts.length > 4) {
            return messageProvider.get("opening.success");
        }

        if (parts[2].equalsIgnoreCase("WINDOW") || parts[2].equalsIgnoreCase("ОКНО")) {
            try {
                int wallNumber = Integer.parseInt(parts[1]);

                if (wallNumber > 6 || wallNumber < 1) {
                    return messageProvider.get("opening.incorrectWallNumber");
                }

                int width = Integer.parseInt(parts[3]);
                int height = Integer.parseInt(parts[4]);
                int distanceFromLeft = Integer.parseInt(parts[5]);
                int distanceFromFloor = Integer.parseInt(parts[6]);

                WallOpening wallOpening = new WallOpening(new Opening(OpeningType.WINDOW, width, height), distanceFromLeft, distanceFromFloor);

                currentRoom = currentRoom.withOpening(wallNumber, wallOpening);
                return messageProvider.get("opening.success");

            } catch (NumberFormatException e) {
                return messageProvider.get("opening.usage");
            }
        }

        if (parts[2].equalsIgnoreCase("DOOR") || parts[2].equalsIgnoreCase("ДВЕРЬ")) {
            try {
                int wallNumber = Integer.parseInt(parts[1]);

                if (wallNumber > 4 || wallNumber < 1) {
                    return messageProvider.get("opening.incorrectWallNumber");
                }

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

    private String handleBudget(String[] parts) {
        if (currentRoom == null) {
            return messageProvider.get("room.not.created");
        }
        FloorType floorType = FloorType.LINOLEUM;
        boolean showTotalOnly = false;

        if (parts.length > 1 && parts.length < 4 ) {
            String arg = parts[1].toLowerCase(Locale.ROOT);
            if (arg.equals("laminate") || arg.equals("ламинат")) {
                floorType = FloorType.LAMINATE;
            } else if (arg.equals("linoleum") || arg.equals("линолеум")) {
                floorType = FloorType.LINOLEUM;
            } else if (arg.equals("total") || arg.equals("итого")) {
                showTotalOnly = true;
                if (parts.length > 2) {
                    String typeArg = parts[2].toLowerCase(Locale.ROOT);
                    if (typeArg.equals("laminate") || typeArg.equals("ламинат")) {
                        floorType = FloorType.LAMINATE;
                    } else if (typeArg.equals("linoleum") || typeArg.equals("линолеум")) {
                        floorType = FloorType.LINOLEUM;
                    }
                }
            } else {
                if (arg.equals("laminate") || arg.equals("ламинат")) {
                    floorType = FloorType.LAMINATE;
                } else if (arg.equals("linoleum") || arg.equals("линолеум")) {
                    floorType = FloorType.LINOLEUM;
                } else {
                    return messageProvider.get("budget.usage");
                }
            }
        }

        var estimate = budgetService.getDetailedEstimate(currentRoom, floorType, locale);

        if (showTotalOnly) {
            return messageProvider.get("budget.total", estimate.totalCost().format());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(messageProvider.get("budget.header", floorType.name().toLowerCase())).append("\n");
            sb.append("• ").append(messageProvider.get("budget.plastering")).append(": ").append(estimate.wallPlasteringCost().format()).append("\n");
            sb.append("• ").append(messageProvider.get("budget.priming")).append(": ").append(estimate.wallPrimingCost().format()).append("\n");
            sb.append("• ").append(messageProvider.get("budget.wallpapering")).append(": ").append(estimate.wallpaperingCost().format()).append("\n");
            sb.append("• ").append(messageProvider.get("budget.floorPouring")).append(": ").append(estimate.floorPouringCost().format()).append("\n");
            sb.append("• ").append(messageProvider.get("budget.floorCovering")).append(": ").append(estimate.floorCoveringCost().format()).append("\n");
            sb.append("• ").append(messageProvider.get("budget.skirting")).append(": ").append(estimate.skirtingBoardCost().format()).append("\n");
            sb.append("• ").append(messageProvider.get("budget.ceiling")).append(": ").append(estimate.ceilingCost().format()).append("\n");
            sb.append("---\n");
            sb.append("💵 ").append(messageProvider.get("budget.total")).append(": ").append(estimate.totalCost().format());
            return sb.toString();
        }
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

}
