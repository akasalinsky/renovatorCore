package org.example.service;

import org.example.model.Room;
import org.example.model.Wall;

public class RoomCalculationService {
    private static final int STANDARD_ROLL_WIDTH = 1060;
    private static final int STANDARD_ROLL_LENGTH = 10000;
    private static final int STANDARD_PLANK_WIDTH = 160;
    private static final int STANDARD_PLANK_LENGTH = 1286;

    public int getFloorArea(Room room) {
        return room.getLength() * room.getWidth();
    }

    public int getVolume(Room room) {
        return getFloorArea(room) * room.height();
    }

    public int getTotalWallArea(Room room) {
        int total = 0;
        for (Wall wall : room.walls()) {
            total += wall.getArea();
        }
        return total;
    }

    public int getTotalOpeningsArea(Room room) {
        int total = 0;
        for (Wall wall : room.walls()) {
            total += wall.totalOpeningsArea();
        }
        return total;
    }

    public int getNetWallArea(Room room) {
        return getTotalWallArea(room) - getTotalOpeningsArea(room);
    }

    public int calculateWallpaperRolls(Room room, int rollWidth, int rollLength) {
        if (rollWidth <= 0 || rollLength <= 0) {
            throw new IllegalArgumentException("Roll dimensions must be positive");
        }
        int rollArea = rollWidth * rollLength;
        return getNetWallArea(room) / rollArea;
    }

    public int calculateWallpaperRolls(Room room) {
        return calculateWallpaperRolls(room, STANDARD_ROLL_WIDTH, STANDARD_ROLL_LENGTH);
    }

    public int calculateLaminatePlanks(Room room, int plankWidth, int plankLength) {
        if (plankWidth <= 0 || plankLength <= 0) {
            throw new IllegalArgumentException("Plank dimensions must be positive");
        }
        int plankArea = plankWidth * plankLength;
        return (getFloorArea(room) + plankArea - 1) / plankArea; // Округление вверх
    }

    public int calculateLaminatePlanks(Room room) {
        return calculateLaminatePlanks(room, STANDARD_PLANK_WIDTH, STANDARD_PLANK_LENGTH);
    }

    public int calculateLinoleumRollLength(Room room, int rollWidth) {
        int max = Math.max(room.getWidth(), room.getLength());
        int min = Math.min(room.getWidth(), room.getLength());

        if (rollWidth <= 0) {
            throw new IllegalArgumentException("Roll dimensions must be positive");
        }
        if (rollWidth < min) { // Исправлено: ширина рулона не может быть меньше минимальной стороны
            throw new IllegalArgumentException("Roll dimension is too small for this room");
        }

        return rollWidth < max ? max : min;
    }
}