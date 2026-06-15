package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Room {
    private String name;
    private int length;
    private int width;
    private int height;
    //private int openingsArea;
    private final int STANDARTROOLWIDTH = 1060;
    private final int STANDARTROOLLENGTH = 10000;
    private final int STANDARTPLANKWIDTH = 160;
    private final int STANDARTPLANKLENGTH = 1286;
    private List<Opening> openings = new ArrayList<>();

    public int getOpeningsArea(){
        int openeingsArea = 0;
        for(Opening opening: openings){
            openeingsArea += opening.area();
        }
        return openeingsArea;
    }


    public Room(String name, int length, int width, int height, int openingsArea) {
        this(name, length, width, height,
                List.of(new Opening(OpeningType.TOTAL_AREA, openingsArea, 1)));
    }

    public Room(String name, int length, int width, int height, List<Opening> openings) {
        if (openings == null) {
            openings = new ArrayList<>();
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }

        int totalWallArea = 2 * (length + width) * height;
        int openingsArea = 0;

        for (Opening opening : openings) {
            openingsArea += opening.area();
        }

        if (openingsArea > totalWallArea) {
            throw new IllegalArgumentException("Openings area cannot exceed total wall area");
        }

        this.name = name;
        this.length = length;
        this.width = width;
        this.height = height;
        this.openings = openings;
    }

    public int getFloorArea() {
        return length * width;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getVolume() {
        return length * width * height;
    }

    public int getTotalWallArea() {
        return 2 * (length + width) * height;
    }

    public int getNetWallArea() {
        return 2 * (length + width) * height - getOpeningsArea();
    }

    public Room withOpeningsArea(int v) {
        int totalWallArea = 2 * (length + width) * height;
        if (getOpeningsArea() > totalWallArea) {
            throw new IllegalArgumentException("Openings area cannot exceed total wall area");
        }

        return new Room(name, length, width, height, v);
    }

    public String describe() {
        return String.format(Locale.US,"Room %1d%2d%3d, openings: %4dm²",
                length, width, height, getOpeningsArea());
    }

    public int getWallpaperRolls(int rollWidth, int rollLength) {
        if (rollWidth <= 0 || rollLength <= 0) {
            throw new IllegalArgumentException("Roll dimensions must be positive");
        }
        int rollArea = rollWidth * rollLength;
        return getNetWallArea() / rollArea;
    }

    public int getWallpaperRolls() {
        int rollArea = STANDARTROOLWIDTH * STANDARTROOLLENGTH;
        return getNetWallArea() / rollArea;
    }

    public int getlaminatePlank(int plankWidth, int plankLength) {
        if (plankWidth <= 0 || plankLength <= 0) {
            throw new IllegalArgumentException("Plank dimensions must be positive");
        }

        int plankArea = plankWidth * plankLength;
        int floorArea = getFloorArea();

        return (floorArea + plankArea - 1) / plankArea;


    }

    public int getlaminatePlank() {
        int plankArea = STANDARTPLANKWIDTH * STANDARTPLANKLENGTH;
        int floorArea = getFloorArea();

        return (floorArea + plankArea - 1) / plankArea;
    }

    public int getLinomeumRollLength(int rollWidth) {
        int max = Math.max(width, length);
        int min = Math.min(width, length);

        if (rollWidth <= 0) {
            throw new IllegalArgumentException("Rool dimensions must be positive");
        }

        if (rollWidth < width && rollWidth < length) {
            throw new IllegalArgumentException("Rool dimensions must be more of room width or length");
        }

        if (rollWidth < max) { return max;}
        else return min;

    }

    public List<Opening> getOpenings() {
        return Collections.unmodifiableList(this.openings);
    }

    public String renderPlan() {
        int width = this.width/1000;
        int length = this.length/1000;

        StringBuilder sb = new StringBuilder();

        // Верхняя граница
        sb.append("+");
        for (int i = 0; i < width; i++) {
            sb.append("-");
        }
        sb.append("+\n");

        // Боковые границы и внутреннее пространство
        for (int i = 0; i < length; i++) {
            sb.append("|");
            for (int j = 0; j < width; j++) {
                sb.append(" ");
            }
            sb.append("|\n");
        }

        // Нижняя граница
        sb.append("+");
        for (int i = 0; i < width; i++) {
            sb.append("-");
        }
        sb.append("+\n");

        return sb.toString();
    }
}
