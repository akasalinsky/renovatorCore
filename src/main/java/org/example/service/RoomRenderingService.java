package org.example.service;

import org.example.model.Room;
import org.example.model.Wall;
import org.example.model.WallOpening;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

public class RoomRenderingService {

    public String describe(Room room, RoomCalculationService calcService) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        if (calcService == null) {
            throw new IllegalArgumentException("calcService cannot be null");
        }
        return String.format(Locale.US, "Room %s: walls count: %d; height: %d, openings: %dm²",
                room.name(),
                room.walls().size(),
                room.height(),
                calcService.getTotalOpeningsArea(room));
    }

    public String renderPlan(Room room, int scale) {
        int w = (room.getWidth() / scale) * 2;
        int h = room.getLength() / scale;
        char[][] canvas = new char[h + 2][w + 2];

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
        if (room.walls().size() > 0) {
            for (WallOpening wo : room.walls().get(0).getWallOpenings()) {
                int start = 1 + (wo.distanceFromLeft()/scale) * 2;
                int opW = (wo.opening().width()/scale) * 2;
                if (opW == 0 && wo.opening().width() > 0) {
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
        if (room.walls().size() > 1) {
            for (WallOpening wo : room.walls().get(1).getWallOpenings()) {
                int start = 1 + wo.distanceFromLeft()/scale;
                int opW = wo.opening().width()/scale;
                if (opW == 0 && wo.opening().width() > 0) {
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
        if (room.walls().size() > 2) {
            for (WallOpening wo : room.walls().get(2).getWallOpenings()) {
                int start = 1 + (wo.distanceFromLeft()/scale) * 2;
                int opW = (wo.opening().width()/scale) * 2;
                if (opW == 0 && wo.opening().width() > 0) {
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
        if (room.walls().size() > 3) {
            for (WallOpening wo : room.walls().get(3).getWallOpenings()) {
                int start = 1 + wo.distanceFromLeft()/scale;
                int opW = wo.opening().width()/scale;
                if (opW == 0 && wo.opening().width() > 0) {
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

        return buildStringFromCanvas(canvas);
    }

    public String renderPlan(Room room) {
        return renderPlan(room,1000);
    }


    public String renderPlanWithNumbers(Room room, int scale) {
        String originalPlan = renderPlan(room, scale);
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
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < newRows; y++) {
            sb.append(canvas[y]).append('\n');
        }

        return sb.toString();
    }

    private String buildStringFromCanvas(char[][] canvas) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : canvas) {
            sb.append(row).append('\n');
        }
        return sb.toString();
    }

    public byte[] renderPlanImage(Room room, int scale) throws IOException {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale must be positive");
        }

        int length = room.getLength(); // мм
        int width = room.getWidth();   // мм

        int margin = 60;
        int pixelWidth = (int) Math.ceil(width / (double) scale);
        int pixelHeight = (int) Math.ceil(length / (double) scale);

        int imageWidth = pixelWidth + 2 * margin;
        int imageHeight = pixelHeight + 2 * margin;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageWidth, imageHeight);

        g.translate(margin, margin);

        g.setStroke(new BasicStroke(3));
        g.setColor(Color.GRAY);

        drawWallWithOpenings(g, room.walls().get(0), 0, 0, pixelWidth, 0, scale, true);
        drawWallWithOpenings(g, room.walls().get(1), pixelWidth, 0, pixelWidth, pixelHeight, scale, false);
        drawWallWithOpenings(g, room.walls().get(2), pixelWidth, pixelHeight, 0, pixelHeight, scale, true);
        drawWallWithOpenings(g, room.walls().get(3), 0, pixelHeight, 0, 0, scale, false);

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(Color.BLUE);

        String widthLabel = width + " мм";
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(widthLabel, g);
        int wX = (pixelWidth - (int) bounds.getWidth()) / 2;
        int wY = pixelHeight + 35;
        g.drawString(widthLabel, wX, wY);

        String lengthLabel = length + " мм";
        Graphics2D g2 = (Graphics2D) g.create();
        g2.rotate(Math.toRadians(-90));
        int lX = -(pixelHeight + (int) bounds.getWidth()) / 2;
        int lY = pixelWidth + 40;
        g2.drawString(lengthLabel, lX, lY);
        g2.dispose();

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));

        g.drawString("1", pixelWidth/2 - 5, -10);
        g.drawString("2", pixelWidth + 10, pixelHeight/2 + 5);
        g.drawString("3", pixelWidth/2 - 5, pixelHeight + 20);
        g.drawString("4",  -19, pixelHeight/2 + 5);


        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private void drawWallWithOpenings(Graphics2D g, Wall wall,
                                      int x1, int y1, int x2, int y2,
                                      int scale, boolean horizontal) {

        Stroke wallStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        Stroke dashStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 5}, 0);

        if (wall.getWallOpenings().isEmpty()) {
            g.drawLine(x1, y1, x2, y2);
            return;
        }

        int currentPos = 0;
        int wallLength = horizontal ? Math.abs(x2 - x1) : Math.abs(y2 - y1);
        int directionX = Integer.compare(x2, x1);
        int directionY = Integer.compare(y2, y1);

        java.util.List<WallOpening> openings = wall.getWallOpenings().stream()
                .sorted((o1, o2) -> Integer.compare(o1.distanceFromLeft(), o2.distanceFromLeft()))
                .toList();

        for (WallOpening wo : openings) {
            int start = (int) Math.round(wo.distanceFromLeft() / (double) scale);
            int openingWidth = (int) Math.round(wo.opening().width() / (double) scale);
            if (openingWidth < 1) openingWidth = 1;

            if (start > currentPos) {
                int endX = horizontal ? x1 + directionX * start : x1;
                int endY = horizontal ? y1 : y1 + directionY * start;
                int startX = horizontal ? x1 + directionX * currentPos : x1;
                int startY = horizontal ? y1 : y1 + directionY * currentPos;

                g.setColor(Color.GRAY);
                g.setStroke(wallStroke);
                g.drawLine(startX, startY, endX, endY);
            }

            g.setColor(Color.WHITE);
            g.setStroke(dashStroke);
            int openStartX = horizontal ? x1 + directionX * start : x1;
            int openStartY = horizontal ? y1 : y1 + directionY * start;
            int openEndX = horizontal ? x1 + directionX * (start + openingWidth) : x1;
            int openEndY = horizontal ? y1 : y1 + directionY * (start + openingWidth);
            g.drawLine(openStartX, openStartY, openEndX, openEndY);

            currentPos = start + openingWidth;
        }

        if (currentPos < wallLength) {
            int endX = horizontal ? x1 + directionX * wallLength : x1;
            int endY = horizontal ? y1 : y1 + directionY * wallLength;
            int startX = horizontal ? x1 + directionX * currentPos : x1;
            int startY = horizontal ? y1 : y1 + directionY * currentPos;

            g.setColor(Color.GRAY);
            g.setStroke(wallStroke);
            g.drawLine(startX, startY, endX, endY);
        }

        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(3));
    }

    private void drawRotatedString(Graphics2D g, String text, int x, int y, double angle) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.rotate(Math.toRadians(angle));
        g2.drawString(text, 0, 0);
        g2.dispose();
    }
}