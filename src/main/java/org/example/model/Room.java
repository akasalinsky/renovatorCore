package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Room(
        String name,
        int height,
        List<Wall> walls,
        List<Integer> angles
) {

    public Room(String name, int height, List<Wall> walls, List<Integer> angles) {
        if (walls == null) {
            throw new IllegalArgumentException("Walls couldn't be null");
        }
        if (walls.isEmpty()) {
            throw new IllegalArgumentException("Walls couldn't be empty");
        }
        for (Wall wall : walls) {
            if (wall == null) {
                throw new IllegalArgumentException("Wall couldn't be null");
            }
        }
        if (angles == null) {
            throw new IllegalArgumentException("Angles couldn't be a null");
        }
        for (Integer integer : angles) {
            if (0 >= integer || integer >= 360) {
                throw new IllegalArgumentException("Angles couldn't be more a 360 or less a 0");
            }
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }

        this.name = name;
        this.height = height;
        this.walls = Collections.unmodifiableList(new ArrayList<>(walls));
        this.angles = Collections.unmodifiableList(new ArrayList<>(angles));
    }

    @JsonIgnore
    public int getLength() {
        return walls.get(1).getLength();
    }

    @JsonIgnore
    public int getWidth() {
        return walls.get(0).getLength();
    }

    public Room withOpening(int number, WallOpening wallOpening) {
        number--;
        List<Wall> newWalls = new ArrayList<>();
        for (Wall wall : walls) {
            if (wall.getCounter() == number) {
                List<WallOpening> newWallOpeningList = new ArrayList<>(wall.getWallOpenings());
                newWallOpeningList.add(wallOpening);
                newWalls.add(new Wall(wall.getLength(), wall.getHeight(), wall.getCounter(), newWallOpeningList));
            } else {
                newWalls.add(wall);
            }
        }
        return new Room(name, height, newWalls, angles);
    }
}