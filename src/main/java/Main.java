package main.java;

import java.io.*;
import java.util.*;

public class Main {

    private List<Rectangle> rectangles = new ArrayList<>();
    private List<Rectangle> rectanglesList = new ArrayList<>();

    public static void main(String[] args) {

        Main computer = new Main();
        long area = computer.computeAreaFromFile("input.txt");
        computer.writeAreaToFile(area, "output.txt");
    }

    public long computeAreaFromFile(String inputFileName) {
        rectangles.clear();
        rectanglesList.clear();

        String line;
        try (BufferedReader inputFileReader = new BufferedReader(new FileReader(inputFileName))) {
            long area = 0;

            while ((line = inputFileReader.readLine()) != null) {
                Rectangle rectangle = Rectangle.fromString(line);
                //вставить проверку на содержание
                boolean sep = true;
                for(Rectangle r: rectanglesList){
                    if (r.contains(rectangle)) sep = false;
                }

                //вставить проверку на содержание
                if(sep) {
                    rectanglesList.add(rectangle);
                    area += addRectangleArea(rectangle, false);
                }
            }

            return area;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Файл не найден");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Файл содержит некорректные данные");
        }
    }

    private int addRectangleArea(Rectangle newRectangle, boolean isIntersection) {
        int result = 0;

        boolean hasIntersections = false;

        for (Rectangle existingRectangle : rectangles) {
                List<Rectangle> complements = existingRectangle.complementOf(newRectangle);
                if (complements.size() > 0) {
                    hasIntersections = true;

                    for (Rectangle complement : complements) {
                        result += addRectangleArea(complement, true);
                    }

                    break;
                }
        }

        if (!hasIntersections) {
            result += newRectangle.area();
        }

        if (!isIntersection) {
            rectangles.add(newRectangle);
        }

        return result;
    }

    private void writeAreaToFile(long area, String outputFileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            writer.write(String.valueOf(area));
        } catch (IOException e) {
            throw new RuntimeException("Нельзя открыть файл " + outputFileName);
        }
    }
}

class Rectangle {
    public final int x1;
    public final int y1;
    public final int x2;
    public final int y2;

    public static Rectangle fromString(String input) throws NumberFormatException, IndexOutOfBoundsException {
        String[] splitInput = input.split(" ");

        if (splitInput.length != 4) {
            throw new IndexOutOfBoundsException();
        }

        return new Rectangle(Integer.valueOf(splitInput[0]),
                Integer.valueOf(splitInput[1]),
                Integer.valueOf(splitInput[2]),
                Integer.valueOf(splitInput[3]));
    }

    public Rectangle(int x1, int y1, int x2, int y2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
    }

    public List<Rectangle> complementOf(Rectangle rectangle) {
        List<Rectangle> intersections = new ArrayList<>();

        if (rectangle.x2 > x1 && x2 > rectangle.x1 && rectangle.y2 > y1 && y2 > rectangle.y1) {
            if (rectangle.y1 <= y1) {
                intersections.add(new Rectangle(rectangle.x1, rectangle.y1, rectangle.x2, y1));
            }

            if (y2 <= rectangle.y2) {
                intersections.add(new Rectangle(rectangle.x1, y2, rectangle.x2, rectangle.y2));
            }

            if (rectangle.x1 <= x1) {
                intersections.add(new Rectangle(rectangle.x1, Math.max(y1, rectangle.y1), x1, Math.min(y2, rectangle.y2)));
            }

            if (x2 <= rectangle.x2) {
                intersections.add(new Rectangle(x2, Math.max(y1, rectangle.y1), rectangle.x2, Math.min(y2, rectangle.y2)));
            }
        }

        return intersections;
    }

    public int area() {
        return Math.abs((x1 - x2) * (y1 - y2));
    }

    public boolean contains(Rectangle rectangle) {
        if(x1 <= rectangle.x1 && rectangle.x2 <= x2 && y1 <= rectangle.y1 && rectangle.y2 <= y2){
            //System.out.println("Содержит");
            return true;
        } else{
            //System.out.println("Не cодержит");
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rectangle)) {
            return false;
        }

        Rectangle other = (Rectangle) o;
        return x1 == other.x1 && y1 == other.y1 && x2 == other.x2 && y2 == other.y2;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 37 * result + x1;
        result = 37 * result + y1;
        result = 37 * result + x2;
        result = 37 * result + y2;

        return result;
    }

    @Override
    public String toString() {
        return String.format("Rectangle with x1: %s y1: %s x2: %s y2: %s", x1, y1, x2, y2);
    }
}