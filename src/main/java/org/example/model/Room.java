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
    private List<Wall> walls = new ArrayList<>();
    private List<Integer> angles = new  ArrayList<>();



    public Room(String name, int height, List<Wall> walls, List<Integer> angles) {
        if (walls == null) {
            throw new IllegalArgumentException("Walls couldn't be null");
        }
        if (walls.isEmpty()) {
            throw new IllegalArgumentException("Walls couldn't be empty");
        }
        for(Wall wall: walls){
            if (wall == null) {
                throw new IllegalArgumentException("Wall couldn't be null");
            }
        }
        if (angles == null) {
            throw new IllegalArgumentException("Angles couldn't be a null");
        }
        for(Integer integer : angles){
            if (0 >= integer || integer >= 360) {
                throw new IllegalArgumentException("Angles couldn't be more a 360 or less a 0");
            }
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }

        this.name = name;
        this.height = height;
        for (Wall wall : walls) {
            wall.setHeight(height);
        }
        this.walls = Collections.unmodifiableList(walls);
        this.angles = Collections.unmodifiableList(angles);
        this.width = walls.get(0).getLength();
        this.length = walls.get(1).getLength();
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
        return 2 * (length + width) * height - totalOpeningsArea();
    }

    public String describe() {
        return String.format(Locale.US,"Room %1s: walls count:%2d; height: %3d, openings: %4dm²",
                name, walls.size(), height, totalOpeningsArea());
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
        return Collections.unmodifiableList(new ArrayList<>());
    }

    public String renderPlan() {
        return renderPlan(1000);
    }

    public String renderPlan(int scale) {
        int w = this.width/scale;   // ширина (восточная/западная стены)
        int h = this.length/scale;  // длина (северная/южная стены)

        // холст: строки 0..h, столбцы 0..w
        char[][] canvas = new char[h + 2][w + 2];

        // заполняем пробелами
        for (int y = 0; y <= h; y++) {
            for (int x = 0; x <= w; x++) {
                canvas[y][x] = ' ';
            }
        }

        // углы
        canvas[0][0] = '+';
        canvas[0][w+1] = '+';
        canvas[h+1][0] = '+';
        canvas[h+1][w+1] = '+';

        // горизонтальные стены (север и юг)
        for (int x = 1; x < w+1; x++) {
            canvas[0][x] = '-';
            canvas[h+1][x] = '-';
        }

        // вертикальные стены (запад и восток)
        for (int y = 1; y < h+1; y++) {
            canvas[y][0] = '|';
            canvas[y][w+1] = '|';
        }

        // проёмы на стенах
        // стена 0: север (y=0, горизонтально)
        if (walls.size() > 0) {
            for (WallOpening wo : walls.get(0).getWallOpenings()) {
                int start = 1 + wo.distanceFromLeft()/1000;
                int opW = wo.opening().getWidth()/1000;
                if (opW == 0 && wo.opening().getWidth() > 0) {
                    opW = 1;
                }
                for (int i = 0; i < opW; i++) {
                    int x = start + i;
                    if (x >= 1 && x < w+1) {
                        canvas[0][x] = ' ';
                    }
                }
            }
        }

        // стена 1: восток (x=w, вертикально)
        if (walls.size() > 1) {
            for (WallOpening wo : walls.get(1).getWallOpenings()) {
                int start = 1 + wo.distanceFromLeft()/1000;
                int opW = wo.opening().getWidth()/1000;
                if (opW == 0 && wo.opening().getWidth() > 0) {
                    opW = 1;
                }
                for (int i = 0; i < opW; i++) {
                    int y = start + i;
                    if (y >= 1 && y < h+1) {
                        canvas[y][w+1] = ' ';
                    }
                }
            }
        }

        // стена 2: юг (y=h, горизонтально)
        if (walls.size() > 2) {
            for (WallOpening wo : walls.get(2).getWallOpenings()) {
                int start = 1 + wo.distanceFromLeft()/1000;
                int opW = wo.opening().getWidth()/1000;
                if (opW == 0 && wo.opening().getWidth() > 0) {
                    opW = 1;
                }
                for (int i = 0; i < opW; i++) {
                    int x = start + i;
                    if (x >= 1 && x < w+1) {
                        canvas[h+1][x] = ' ';
                    }
                }
            }
        }

        // стена 3: запад (x=0, вертикально)
        if (walls.size() > 3) {
            for (WallOpening wo : walls.get(3).getWallOpenings()) {
                int start = 1 + wo.distanceFromLeft()/1000;
                int opW = wo.opening().getWidth()/1000;
                if (opW == 0 && wo.opening().getWidth() > 0) {
                    opW = 1;
                }
                for (int i = 0; i < opW; i++) {
                    int y = start + i;
                    if (y >= 1 && y < h+1) {
                        canvas[y][0] = ' ';
                    }
                }
            }
        }

        // сборка строки
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y <= h+1; y++) {
            for (int x = 0; x <= w+1; x++) {
                sb.append(canvas[y][x]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public List<Wall> getWalls() {
        return Collections.unmodifiableList(walls);
    }

    public List<Integer> getAngles() {
        return Collections.unmodifiableList(angles);
    }

    public int totalWallArea() {
        int totalWallArea = 0;
        if (!walls.isEmpty()){
            for(Wall wall: walls){
                totalWallArea += wall.getArea();
            }
        }
        return totalWallArea;
    }

    public int totalOpeningsArea() {
        int totalOpeningsArea = 0;
        if (!walls.isEmpty()){
            for(Wall wall: walls){
                totalOpeningsArea += wall.totalOpeningsArea();
            }
        }
        return totalOpeningsArea;
    }

    public int netWallArea() {
        int netWallArea = 0;
        if (!walls.isEmpty()){
            for(Wall wall: walls){
                netWallArea += wall.netArea();
            }
        }
        return netWallArea;
    }

    public Room withOpening(int i, WallOpening wallOpening) {
        throw new UnsupportedOperationException("Adding openings to walls is not yet supported.");
    }
}
