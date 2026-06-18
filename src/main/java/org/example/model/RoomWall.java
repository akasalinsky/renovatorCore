package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class RoomWall {
    private final int STANDARTROOLWIDTH = 1060;
    private final int STANDARTROOLLENGTH = 10000;
    private final int STANDARTPLANKWIDTH = 160;
    private final int STANDARTPLANKLENGTH = 1286;

    private String name;
    private int height;
    private List<Wall> walls = new ArrayList<>();


    public RoomWall(String name, int height, List<Wall> walls) {
        if (walls == null || walls.isEmpty()){
            throw new IllegalArgumentException("Walls couldn't be null or empty");
        }
        for(Wall wall: walls){
            if (wall == null) {
                throw new IllegalArgumentException("Wall couldn't be null");
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("Name must be correct");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }

        this.name = name;
        this.height = height;
        this.walls = walls;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public int getTotalWallArea() {
        int totalLenght = 0;
        for (Wall wall: walls){
            totalLenght += wall.getLength();
        }
        return totalLenght * height;
    }

    public int getTotalOpeningsArea() {
        int totalArea = 0;
        for (Wall wall: walls){
            for(WallOpening opening: wall.getWallOpenings()){
                totalArea += opening.area();
            }
        }
        return totalArea;
    }

    public int netWallArea() {
        return getTotalWallArea() - getTotalOpeningsArea();
    }
}
