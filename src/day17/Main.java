package day17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {

        var input = Files.readString(Path.of("src/day17/input.txt"));

        System.out.printf("Part 1: %d\n", simulate(input, 6, true));
        System.out.printf("Part 2: %d\n", simulate(input, 6, false));
    }

    private static int simulate(String input, int steps, boolean restrictTo3D) {
        // other offset values would do, as long as there's enough space in the grid
        var grid = new Grid(input, 25, restrictTo3D);

        for (var step = 0; step < steps; step++) {
            var ranges = grid.getRanges();
            for (var w = ranges.w().min(); w <= ranges.w().max(); w++) {
                for (var z = ranges.z().min(); z <= ranges.z().max(); z++) {
                    for (var y = ranges.y().min(); y <= ranges.y().max(); y++) {
                        for (var x = ranges.x().min(); x <= ranges.x().max(); x++) {
                            var active = grid.get(x, y, z, w);
                            var adj = grid.adjacent(x, y, z, w);
                            if (active) grid.set(x, y, z, w, adj == 2 || adj == 3);
                            else grid.set(x, y, z, w, adj == 3);
                        }
                    }
                }
            }
            grid.endStep();
        }

        var ranges = grid.getRanges(); var count = 0;
        for (var w=ranges.w().min(); w<=ranges.w().max(); w++) {
            for (var z = ranges.z().min(); z <= ranges.z().max(); z++) {
                for (var y = ranges.y().min(); y <= ranges.y().max(); y++) {
                    for (var x = ranges.x().min(); x <= ranges.x().max(); x++) {
                        if (grid.get(x, y, z, w)) count++;
                    }
                }
            }
        }

        return count;
    }

    private record Range(int min, int max) { }

    private record Ranges(Range x, Range y, Range z, Range w) { }

    private static class Grid {
        private boolean[][][][] current;
        private boolean[][][][] next;
        private int lowestX, highestX; // real array positions
        private int lowestY, highestY; // real array positions
        private int lowestZ, highestZ; // real array positions
        private int lowestW, highestW; // real array positions
        private final int offset;
        private final boolean is3D;

        /**
         * offset = how much space there should be between the origin and the negative limit of an axis.
         *          offset of 30 -> lowest point is (-30, -30, -30, (-30))
         */
        public Grid(String input, int offset, boolean restrictTo3D) {
            var size = offset*2 + 1;
            current = new boolean[size][size][size][size];
            next = new boolean[size][size][size][size];
            var lines = input.split("\r\n");
            lowestX = size; highestX = 0;
            lowestY = size; highestY = 0;
            lowestZ = offset; highestZ = offset;
            lowestW = offset; highestW = offset;
            initFillArray(current, lines, offset);
            this.offset = offset;
            this.is3D = restrictTo3D;
        }

        // center contents on (offset, offset) 2D point. positive x goes right, positive y goes down
        private void initFillArray(boolean[][][][] array, String[] input, int offset) {
            var topLeft = offset - input.length/2;
            for (var y=0; y<input.length; y++) {
                for (var x=0; x<input.length; x++) {
                    var xIndex = topLeft + x;
                    var yIndex = topLeft + y;
                    if (input[y].charAt(x) == '#') {
                        array[offset][offset][yIndex][xIndex] = true;
                        lowestX = Math.min(lowestX, xIndex); highestX = Math.max(highestX, xIndex);
                        lowestY = Math.min(lowestY, yIndex); highestY = Math.max(highestY, yIndex);
                    }
                }
            }
        }

        public int adjacent(int x, int y, int z, int w) {
            var xIndex = x+offset; var yIndex = y+offset; var zIndex = z+offset; var wIndex = w+offset;
            int count = 0, limit = current[wIndex][zIndex][yIndex][xIndex] ? 5 : 4;

            for (var realW = is3D ? offset : wIndex-1; realW <= (is3D ? offset : wIndex+1); realW++) {
                for (var realZ = zIndex - 1; realZ <= zIndex + 1; realZ++) {
                    for (var realY = yIndex - 1; realY <= yIndex + 1; realY++) {
                        for (var realX = xIndex - 1; realX <= xIndex + 1; realX++) {
                            if (current[realW][realZ][realY][realX] && ++count >= limit)
                                return current[wIndex][zIndex][yIndex][xIndex] ? count - 1 : count; // subtract self?
                        }
                    }
                }
            }

            return current[wIndex][zIndex][yIndex][xIndex] ? count-1 : count; // subtract self?
        }

        public void set(int x, int y, int z, int w, boolean value) {
            var xIndex = x+offset; var yIndex = y+offset; var zIndex = z+offset; var wIndex = w+offset;
            next[wIndex][zIndex][yIndex][xIndex] = value;

            if (value) {
                lowestX = Math.min(lowestX, xIndex); highestX = Math.max(highestX, xIndex);
                lowestY = Math.min(lowestY, yIndex); highestY = Math.max(highestY, yIndex);
                lowestZ = Math.min(lowestZ, zIndex); highestZ = Math.max(highestZ, zIndex);
                lowestW = Math.min(lowestW, wIndex); highestW = Math.max(highestW, wIndex);
            }
        }

        public boolean get(int x, int y, int z, int w) {
            return current[w+offset][z+offset][y+offset][x+offset];
        }

        public void endStep() { var tmp = current; current = next; next = tmp; }

        public Ranges getRanges() {
            return new Ranges(new Range(lowestX-offset-1, highestX-offset+1),
                              new Range(lowestY-offset-1, highestY-offset+1),
                              new Range(lowestZ-offset-1, highestZ-offset+1),
                              new Range(is3D ? 0 : lowestW-offset-1, is3D ? 0 : highestW-offset+1));
        }

    }

}