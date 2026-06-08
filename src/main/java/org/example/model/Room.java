package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Room {
    private String name;
    private double length;
    private double width;
    private double height;
    //private double openingsArea;
    private final double STANDARTROOLWIDTH = 1.06;
    private final double STANDARTROOLLENGTH = 10.0;
    private final double STANDARTPLANKWIDTH = 0.16;
    private final double STANDARTPLANKLENGTH = 1.286;
    private List<Opening> openings = new ArrayList<>();

    public double getOpeningsArea(){
        double openeingsArea = 0.0;
        for(Opening opening: openings){
            openeingsArea += opening.area();
        }
        return openeingsArea;
    }


    public Room(String name, double length, double width, double height, double openingsArea) {
        this(name, length, width, height,
                List.of(new Opening(OpeningType.TOTAL_AREA, openingsArea, 1.0)));
    }

    public Room(String name, double length, double width, double height, List<Opening> openings) {
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

        double totalWallArea = 2 * (length + width) * height;
        double openingsArea = 0;

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

    public double getFloorArea() {
        return length * width;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getVolume() {
        return length * width * height;
    }

    public double getTotalWallArea() {
        return 2 * (length + width) * height;
    }

    public double getNetWallArea() {
        return 2 * (length + width) * height - getOpeningsArea();
    }

    public Room withOpeningsArea(double v) {
        double totalWallArea = 2 * (length + width) * height;
        if (getOpeningsArea() > totalWallArea) {
            throw new IllegalArgumentException("Openings area cannot exceed total wall area");
        }

        return new Room(name, length, width, height, v);
    }

    public String describe() {
        return String.format(Locale.US,"Room %.1fx%.1fx%.1f, openings: %.2f m²",
                length, width, height, getOpeningsArea());
    }

    public int getWallpaperRolls(double rollWidth, double rollLength) {
        if (rollWidth <= 0 || rollLength <= 0) {
            throw new IllegalArgumentException("Roll dimensions must be positive");
        }
        double rollArea = rollWidth * rollLength;
        return (int) Math.ceil(getNetWallArea() / rollArea);
    }

    public int getWallpaperRolls() {
        double rollArea = STANDARTROOLWIDTH * STANDARTROOLLENGTH;
        return (int) Math.ceil(getNetWallArea() / rollArea);
    }

    public int getlaminatePlank(double plankWidth, double plankLength) {
        if (plankWidth <= 0 || plankLength <= 0) {
            throw new IllegalArgumentException("Plank dimensions must be positive");
        }
        double plankArea = plankWidth * plankLength;
        return (int) Math.ceil(getFloorArea() / plankArea);
    }

    public int getlaminatePlank() {
        double plankArea = STANDARTPLANKWIDTH * STANDARTPLANKLENGTH;
        return (int) Math.ceil(getFloorArea() / plankArea);
    }

    public double getLinomeumRollLength(double rollWidth) {
        double max = Math.max(width, length);
        double min = Math.min(width, length);

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
}
