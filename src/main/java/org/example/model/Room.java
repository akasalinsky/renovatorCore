package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Room {
    private final String name;
    private final int height;
    @JsonIgnore private final int STANDARTROOLWIDTH = 1060;
    @JsonIgnore private final int STANDARTROOLLENGTH = 10000;
    @JsonIgnore private final int STANDARTPLANKWIDTH = 160;
    @JsonIgnore private final int STANDARTPLANKLENGTH = 1286;
    private final List<Wall> walls;
    private final List<Integer> angles;

    @JsonIgnore private final int length;
    @JsonIgnore private final int width;


    public Room(
            @JsonProperty("name") String name,
            @JsonProperty("height") int height,
            @JsonProperty("walls") List<Wall> walls,
            @JsonProperty("angles") List<Integer> angles) {
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
        //int[] counter = {1};
        this.walls = walls;
                /*.stream()
                .map(wall -> {
                    if (wall == null) throw new IllegalArgumentException("Wall cannot be null");
                    return wall.withCounter(counter[0]++);
                })
                .toList();*/
        this.angles = Collections.unmodifiableList(angles);
        this.width = walls.get(0).getLength();
        this.length = walls.get(1).getLength();
    }

    @JsonIgnore
    public int getFloorArea() {
        return length * width;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public int getLength() {
        return length;
    }

    @JsonIgnore
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @JsonIgnore
    public int getVolume() {
        return length * width * height;
    }

    @JsonIgnore
    public int getTotalWallArea() {
        return 2 * (length + width) * height;
    }

    @JsonIgnore
    public int getNetWallArea() {
        return 2 * (length + width) * height - totalOpeningsArea();
    }

    @JsonIgnore
    public int getWallpaperRolls(int rollWidth, int rollLength) {
        if (rollWidth <= 0 || rollLength <= 0) {
            throw new IllegalArgumentException("Roll dimensions must be positive");
        }
        int rollArea = rollWidth * rollLength;
        return getNetWallArea() / rollArea;
    }

    @JsonIgnore
    public int getWallpaperRolls() {
        int rollArea = STANDARTROOLWIDTH * STANDARTROOLLENGTH;
        return getNetWallArea() / rollArea;
    }

    @JsonIgnore
    public int getlaminatePlank(int plankWidth, int plankLength) {
        if (plankWidth <= 0 || plankLength <= 0) {
            throw new IllegalArgumentException("Plank dimensions must be positive");
        }

        int plankArea = plankWidth * plankLength;
        int floorArea = getFloorArea();

        return (floorArea + plankArea - 1) / plankArea;
    }

    @JsonIgnore
    public int getlaminatePlank() {
        int plankArea = STANDARTPLANKWIDTH * STANDARTPLANKLENGTH;
        int floorArea = getFloorArea();

        return (floorArea + plankArea - 1) / plankArea;
    }

    @JsonIgnore
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

    public List<Wall> getWalls() {
        return Collections.unmodifiableList(walls);
    }

    public List<Integer> getAngles() {
        return Collections.unmodifiableList(angles);
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
                int start = 1 + wo.distanceFromLeft()/scale;
                int opW = wo.opening().getWidth()/scale;
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
                int start = 1 + wo.distanceFromLeft()/scale;
                int opW = wo.opening().getWidth()/scale;
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
                int start = 1 + wo.distanceFromLeft()/scale;
                int opW = wo.opening().getWidth()/scale;
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
                int start = 1 + wo.distanceFromLeft()/scale;
                int opW = wo.opening().getWidth()/scale;
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

    public String renderPlanWithNumbers(int scale) {
        String originalPlan = renderPlan(scale);

        String[] lines = originalPlan.split("\\n");
        if (lines.length == 0 || lines[0].isEmpty()) {
            return originalPlan;
        }

        int oldRows = lines.length;
        int oldCols = lines[0].length();

        // 3. Создаем новый холст: +5 строк, +4 столбца
        int newRows = oldRows + 5;
        int newCols = oldCols + 3;
        char[][] canvas = new char[newRows][newCols];

        // Заполняем весь новый холст пробелами
        for (int y = 0; y < newRows; y++) {
            java.util.Arrays.fill(canvas[y], ' ');
        }

        // 4. Копируем символы оригинального плана со сдвигом на 2 вправо и 1 вниз
        for (int y = 0; y < oldRows; y++) {
            char[] rowChars = lines[y].toCharArray();
            System.arraycopy(rowChars, 0, canvas[y + 2], 2, rowChars.length);
        }

        // 5. Вычисляем визуальный центр коробки плана
        int visualCenterCol = 1 + (oldCols / 2);
        int visualCenterRow = 1 + (oldRows / 2);

        // 6. ИСПРАВЛЕНО: Строгая запись в конкретные ячейки [y][x] (обе координаты!)
        canvas[1][visualCenterCol] = '1';               // Верх (Строка 0, точный центр)
        canvas[visualCenterRow + 1][newCols - 1] = '2';     // Право (Центр, последний столбец)
        canvas[newRows - 3][visualCenterCol] = '3';     // Низ (Последняя строка, точный центр)
        canvas[visualCenterRow + 1][0] = '4';               // Лево (Центр, самый первый столбец 0)


        // 7. Собираем массив обратно в итоговую строку
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < newRows; y++) {
            sb.append(canvas[y]).append('\n');
        }

        return sb.toString();
    }

    public String describe() {
        return String.format(Locale.US,"Room %1s: walls count:%2d; height: %3d, openings: %4dm²",
                name, walls.size(), height, totalOpeningsArea());
    }

    public Room withOpening(int number, WallOpening wallOpening) {
        List<WallOpening> newWallsOpening = new java.util.ArrayList<>();
        List<Wall> newWalls = new ArrayList<>();
        for (Wall wall : walls) {
            if (wall.getCounter() == number) {
                List<WallOpening> newWallOpeningList = new java.util.ArrayList<>(wall.getWallOpenings());
                newWallOpeningList.add(wallOpening);
                newWalls.add(new Wall(wall.getLength(), wall.getHeight(), wall.getCounter(), newWallOpeningList));
            } else {
                newWalls.add(wall);
            }
        }
        return new Room(name, height, newWalls, angles);
    }

    @JsonIgnore
    public int totalWallArea() {
        int totalWallArea = 0;
        if (!walls.isEmpty()){
            for(Wall wall: walls){
                totalWallArea += wall.getArea();
            }
        }
        return totalWallArea;
    }

    @JsonIgnore
    public int totalOpeningsArea() {
        int totalOpeningsArea = 0;
        if (!walls.isEmpty()){
            for(Wall wall: walls){
                totalOpeningsArea += wall.totalOpeningsArea();
            }
        }
        return totalOpeningsArea;
    }

    @JsonIgnore
    public int netWallArea() {
        int netWallArea = 0;
        if (!walls.isEmpty()){
            for(Wall wall: walls){
                netWallArea += wall.netArea();
            }
        }
        return netWallArea;
    }

    @JsonIgnore
    private Wall getWallByNumber(int number){
        if(number < 1 || number > 4){throw new IllegalArgumentException("Wall number must be 1 or 2 or 3 or 4");}
        for(Wall wall: walls){
            if (wall.getCounter() == number) {return wall;}
        }
        throw new IllegalArgumentException("Required wall with number " + number + " not found.");
    }
}
