package day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var ship = new Ship(Files.readAllLines(Path.of("src/day12/input.txt")).stream()
                .map(line -> new Instruction(line.substring(0, 1), Integer.parseInt(line.substring(1))))
                .collect(Collectors.toList()));

        System.out.printf("Part 1: %d\n", manhattan(ship, ship::act));
        System.out.printf("Part 2: %d\n", manhattan(ship, ship::actWithWaypoint));
    }

    private static int manhattan(Ship ship, BiConsumer<String, Integer> strategy) {
        ship.reset();
        ship.instructions.forEach(ins -> strategy.accept(ins.action(), ins.value()));
        return Math.abs(ship.x) + Math.abs(ship.y);
    }

    private enum Direction { N, E, S, W }

    private record Instruction(String action, int value) { }

    // east (x) positive, north (y) positive
    private static class Ship {
        private Direction[] directions;
        private int dirIndex;
        private int wx, wy;
        public List<Instruction> instructions;
        public int x, y;

        public Ship(List<Instruction> input) {
            directions = Direction.values();
            instructions = input;
        }

        public void reset() {
            dirIndex = 1;
            wx = 10; wy = 1;
            x = 0; y = 0;
        }

        public void act(String action, int value) {
            switch (action) {
                case "N" -> y += value;
                case "S" -> y -= value;
                case "E" -> x += value;
                case "W" -> x -= value;
                case "L" -> { dirIndex -= value/90; if (dirIndex < 0) dirIndex = 4 + dirIndex; }
                case "R" -> dirIndex = (dirIndex + value/90) % 4;
                default -> act(directions[dirIndex].name(), value);
            }
        }

        public void actWithWaypoint(String action, int value) {
            switch (action) {
                case "N" -> wy += value;
                case "S" -> wy -= value;
                case "E" -> wx += value;
                case "W" -> wx -= value;
                case "L" -> rotateWaypoint(value, false);
                case "R" -> rotateWaypoint(value, true);
                default -> { x += wx * value; y += wy * value; }
            }
        }

        private void rotateWaypoint(int value, boolean clockwise) {
            switch (value) {
                case 90 -> { var tmp = wy; wy = clockwise ? -wx : wx; wx = clockwise ? tmp : -tmp; }
                case 180 -> { wx = -wx; wy = -wy; }
                case 270 -> { var tmp = wy; wy = clockwise ? wx : -wx; wx = clockwise ? -tmp : tmp;  }
            }
        }
    }

}