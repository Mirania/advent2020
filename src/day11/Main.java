package day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var grid = new Grid(Files.readString(Path.of("src/day11/input.txt")));

        System.out.printf("Part 1: %d\n", simulate(grid, grid::adjacent, 4));
        System.out.printf("Part 2: %d\n", simulate(grid, grid::visible, 5));
    }

    private static int simulate(Grid grid, BiFunction<Integer, Integer, Integer> strategy, int tolerance) {
        var eq = false;
        while (!eq) {
            eq = true;
            for (var y=0; y<grid.rows; y++) {
                for (var x=0; x<grid.columns; x++) {
                    var tile = grid.get(x, y);
                    if (tile == Tile.Floor) continue;
                    else if (tile == Tile.Empty && strategy.apply(x,y) == 0) { grid.set(x, y, Tile.Occupied); eq = false; }
                    else if (tile == Tile.Occupied && strategy.apply(x,y) == tolerance) { grid.set(x, y, Tile.Empty); eq = false; }
                    else grid.set(x, y, tile);
                }
            }
            grid.endStep();
        }

        var occupied = 0;
        for (var y=0; y<grid.rows; y++) {
            for (var x=0; x<grid.columns; x++) {
                if (grid.get(x, y) == Tile.Occupied) occupied++;
            }
        }
        return occupied;
    }

    private enum Tile { Floor, Empty, Occupied, Wall }

    private enum Direction { NW, N, NE, E, SE, S, SW, W };

    // positive x goes right, positive y goes down. (x,y) to check a point, starts at (0,0)
    private static class Grid {
        private List<List<Tile>> current;
        private List<List<Tile>> next;
        public int rows, columns;

        public Grid(String input) {
            current = input.lines()
                    .map(line -> line.chars()
                            .mapToObj(ch -> ch == '.' ? Tile.Floor : ch == 'L' ? Tile.Empty : Tile.Occupied)
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());
            next = current.stream().map(ArrayList::new).collect(Collectors.toList());
            rows = current.size();
            columns = current.get(0).size();
        }

        private Tile relative(int x, int y, Direction direction, int offset) {
            return switch (direction) {
                case NW -> get(x-offset, y-offset);
                case N -> get(x, y-offset);
                case NE -> get(x+offset, y-offset);
                case E -> get(x+offset, y);
                case SE -> get(x+offset, y+offset);
                case S -> get(x, y+offset);
                case SW -> get(x-offset, y+offset);
                case W -> get(x-offset, y);
            };
        }

        public int adjacent(int x, int y) {
            var count = 0;
            for (var dir : Direction.values()) {
                if (count == 4) break;
                if (relative(x, y, dir, 1) == Tile.Occupied) count++;
            }
            return count;
        }

        public int visible(int x, int y) {
            int count = 0, offset; Tile tile;
            for (var dir : Direction.values()) {
                if (count == 5) break; offset = 1;
                do { if ((tile = relative(x, y, dir, offset++)) == Tile.Occupied) count++; } while (tile == Tile.Floor);
            }
            return count;
        }

        public void set(int x, int y, Tile tile) {
            if (x < 0 || x >= columns || y < 0 || y >= rows) return;
            next.get(y).set(x, tile);
        }

        public Tile get(int x, int y) {
            if (x < 0 || x >= columns || y < 0 || y >= rows) return Tile.Wall;
            return current.get(y).get(x);
        }

        public void endStep() { var tmp = current; current = next; next = tmp; }

        // method for debugging
        public String toString() {
            var sb = new StringBuilder((columns + 1) * rows);
            for (var y : current) {
                for (var x : y) sb.append(x == Tile.Floor ? '.' : x == Tile.Empty ? 'L' : '#');
                sb.append("\n");
            }
            return sb.toString();
        }
    }

}