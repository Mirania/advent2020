package day24;

import utils.RegexMatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static RegexMatcher instructionMatcher = new RegexMatcher("se|sw|ne|nw|e|w");

    public static void main(String[] args) throws IOException {

        var instructionList = Files.readAllLines(Path.of("src/day24/input.txt")).stream()
                .map(line -> instructionMatcher.reset(line).getAllMatches())
                .collect(Collectors.toList());

        System.out.printf("Part 1: %d\n", simulate(instructionList, 0));
        System.out.printf("Part 2: %d\n", simulate(instructionList, 100));
    }

    private static int simulate(List<List<String>> instructionList, int steps) {
        var grid = new Grid(instructionList, 125);

        for (var step = 0; step < steps; step++) {
            var ranges = grid.getRanges();
            for (var y = ranges.y().min(); y <= ranges.y().max(); y++) {
                for (var x = ranges.x().min(); x <= ranges.x().max(); x++) {
                    if (!isValidHex(x, y)) continue;
                    var black = grid.get(x, y);
                    var adj = grid.adjacent(x, y);
                    if (black) grid.set(x, y, adj == 1 || adj == 2);
                    else grid.set(x, y, adj == 2);
                }
            }
            grid.endStep();
        }

        var ranges = grid.getRanges(); var count = 0;
        for (var y = ranges.y().min(); y <= ranges.y().max(); y++) {
            for (var x = ranges.x().min(); x <= ranges.x().max(); x++) {
                if (!isValidHex(x, y)) continue;
                if (grid.get(x, y)) count++;
            }
        }

        return count;
    }

    private record Point(int x, int y) { }

    private record Range(int min, int max) { }

    private record Ranges(Range x, Range y) { }

    private enum Direction { SE, SW, NE, NW, E, W }

    // positive x goes right, positive y goes up
    private static Point followInstructions(List<String> instructions) {
        int x = 0, y = 0;
        for (var ins : instructions) {
            switch (ins) {
                case "se" -> { x++; y--; }
                case "sw" -> { x--; y--; }
                case "ne" -> { x++; y++; }
                case "nw" -> { x--; y++; }
                case "e" -> x += 2;
                case "w" -> x -= 2;
            }
        }
        return new Point(x, y);
    }

    private static boolean isValidHex(int x, int y) {
        return Math.abs(y % 2) == Math.abs(x % 2);
    }

    private static class Grid {
        private boolean[][] current;
        private boolean[][] next;
        private int lowestX, highestX; // real array positions
        private int lowestY, highestY; // real array positions
        private final int offset;

        /**
         * offset = how much space there should be between the origin and the negative limit of an axis.
         *          offset of 30 -> lowest point is (-30, -30)
         */
        public Grid(List<List<String>> instructionList, int offset) {
            var size = offset*2 + 1;
            current = new boolean[size][size];
            next = new boolean[size][size];
            lowestX = size; highestX = 0;
            lowestY = size; highestY = 0;
            initFillArray(current, instructionList, offset);
            this.offset = offset;
        }

        // center contents on (offset, offset) 2D point. positive x goes right, positive y goes down
        private void initFillArray(boolean[][] array, List<List<String>> instructionList, int offset) {
            instructionList.forEach(instructions -> {
                var point = followInstructions(instructions);
                int xIndex = point.x()+offset, yIndex = point.y()+offset;
                array[yIndex][xIndex] = !array[yIndex][xIndex];
                lowestX = Math.min(lowestX, xIndex); highestX = Math.max(highestX, xIndex);
                lowestY = Math.min(lowestY, yIndex); highestY = Math.max(highestY, yIndex);
            });
        }

        private void requireValidPoint(int x, int y) {
            if (!isValidHex(x, y))
                throw new IllegalArgumentException("invalid hexagon position ("+x+","+y+")");
        }

        private boolean relative(int x, int y, Direction direction) {
            return switch (direction) {
                case SE -> get(x+1, y-1);
                case SW -> get(x-1, y-1);
                case NE -> get(x+1, y+1);
                case NW -> get(x-1, y+1);
                case E -> get(x+2, y);
                case W -> get(x-2, y);
            };
        }

        public int adjacent(int x, int y) {
            requireValidPoint(x, y);
            int count = 0;
            for (var dir : Direction.values()) {
                if (count == 3) break;
                if (relative(x, y, dir)) count++;
            }
            return count;
        }

        public void set(int x, int y, boolean value) {
            requireValidPoint(x, y);
            var xIndex = x+offset; var yIndex = y+offset;
            next[yIndex][xIndex] = value;

            if (value) {
                lowestX = Math.min(lowestX, xIndex); highestX = Math.max(highestX, xIndex);
                lowestY = Math.min(lowestY, yIndex); highestY = Math.max(highestY, yIndex);
            }
        }

        public boolean get(int x, int y) {
            requireValidPoint(x, y);
            return current[y+offset][x+offset];
        }

        public void endStep() { var tmp = current; current = next; next = tmp; }

        public Ranges getRanges() {
            return new Ranges(new Range(lowestX-offset-1, highestX-offset+1),
                              new Range(lowestY-offset-1, highestY-offset+1));
        }

    }
}