package day20;

import utils.RegexMatcher;
import utils.RegexMatcher.IntRegexMatcher;
import utils.Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// perhaps the least fun i've ever had while programming
public class Main {

    private static IntRegexMatcher matcher = RegexMatcher.intExtractor();

    public static void main(String[] args) throws IOException {

        var tiles = Arrays.stream(Files.readString(Path.of("src/day20/input.txt")).split("\r\n\r\n"))
                .map(Tile::new).collect(Collectors.toList());

        System.out.printf("Part 1: %d\n", multiplyCorners(tiles));
        System.out.printf("Part 2: %d\n", getWaterRoughness(tiles));
    }

    private static long multiplyCorners(List<Tile> tiles) {
        var connections = getConnections(tiles);
        return connections.keySet().parallelStream()
                .filter(tile -> connections.get(tile).size() == 2)
                .mapToLong(tile -> tile.id)
                .reduce(1, (a, b) -> a * b);
    }

    private static int getWaterRoughness(List<Tile> tiles) {
        var built = reassemble(tiles);

        var top = new RegexMatcher(".{18}#.{1}");
        var middle = new RegexMatcher("#.{4}##.{4}##.{4}###");
        var bottom = new RegexMatcher(".{1}#.{2}#.{2}#.{2}#.{2}#.{2}#.{3}");

        var count = 0;
        for (var reposition : repositions()) {
            reposition.accept(built);
            count = findMonsters(top, middle, bottom, built);
            if (count > 0) break;
        }

        return new RegexMatcher("#", built.gridToString()).countMatches() - count * 15;
    }

    private record PuzzleMatch(Tile placed, Tile toPlace) {}

    private record BuildPiece(Tile tile, int row, int col) { }

    private static int findMonsters(RegexMatcher top, RegexMatcher middle, RegexMatcher bottom, Tile built) {
        var lines = built.gridToString().split("\n");
        var count = 0;

        for (var i=1; i<lines.length-1; i++) {
            middle.reset(lines[i]);
            int idx, lineStart = 0;
            while ((idx = middle.getNextPosition()) != -1) {
                top.reset(lines[i-1].substring(idx+lineStart, idx+lineStart+20));
                bottom.reset(lines[i+1].substring(idx+lineStart, idx+lineStart+20));
                if (top.matches() && bottom.matches()) count++;

                lineStart += idx + 1;
                middle.reset(lines[i].substring(lineStart));
            }
        }

        return count;
    }

    private static Tile reassemble(List<Tile> tiles) {
        var connections = getConnections(tiles);
        var puzzleSize = Double.valueOf(Math.sqrt(tiles.size())).intValue();

        // pick any corner, doesn't matter
        var corner = connections.keySet().parallelStream()
                .filter(tile -> connections.get(tile).size() == 2).findAny().get();

        // figure out relative tile positions
        var placed = new HashSet<Tile>();
        var toCheck = new ArrayDeque<PuzzleMatch>();
        placed.add(corner);

        for (var next : connections.get(corner))
            toCheck.add(new PuzzleMatch(corner, next));

        while (toCheck.size() > 0) {
            var match = toCheck.pop();
            if (placed.contains(match.toPlace())) continue;
            relative(match.placed, match.toPlace());

            for (var next : connections.get(match.toPlace)) {
                if (!placed.contains(next)) toCheck.add(new PuzzleMatch(match.toPlace(), next));
            }

            placed.add(match.toPlace());
        }

        // arrange tiles in grid
        var reassembled = new Tile[puzzleSize][puzzleSize];
        var toBuild = new ArrayDeque<BuildPiece>();

        if (corner.right != null && corner.bottom != null) toBuild.add(new BuildPiece(corner, 0, 0));
        else if (corner.left != null && corner.bottom != null) toBuild.add(new BuildPiece(corner, 0, puzzleSize-1));
        else if (corner.right != null && corner.top != null) toBuild.add(new BuildPiece(corner, puzzleSize-1, 0));
        else toBuild.add(new BuildPiece(corner, puzzleSize-1, puzzleSize-1));

        while (toBuild.size() > 0) {
            var piece = toBuild.pop();
            if (piece.row < 0 || piece.row >= reassembled.length ||
                piece.col < 0 || piece.col >= reassembled.length || reassembled[piece.row][piece.col] != null) continue;

            reassembled[piece.row][piece.col] = piece.tile;
            if (piece.tile.left != null) toBuild.add(new BuildPiece(piece.tile.left, piece.row, piece.col-1));
            if (piece.tile.right != null) toBuild.add(new BuildPiece(piece.tile.right, piece.row, piece.col+1));
            if (piece.tile.top != null) toBuild.add(new BuildPiece(piece.tile.top, piece.row-1, piece.col));
            if (piece.tile.bottom != null) toBuild.add(new BuildPiece(piece.tile.bottom, piece.row+1, piece.col));
        }

        // convert tile refs into a single image
        var expanded = new char[8*puzzleSize][8*puzzleSize];

        for (var row=0; row<puzzleSize; row++) {
            for (var col=0; col<puzzleSize; col++) {
                int rowStart = row*8, colStart = col*8;
                var tile = reassembled[row][col];
                for (var y=1; y<tile.grid.length-1; y++) {
                    for (var x=1; x<tile.grid.length-1; x++)
                        expanded[rowStart+y-1][colStart+x-1] = tile.grid[y][x];
                }
            }
        }

        return new Tile(expanded);
    }

    private static Map<Tile, List<Tile>> getConnections(List<Tile> tiles) {
        var map = new HashMap<Tile, List<Tile>>();

        for (var tile : tiles) {
            for (var other : tiles) {
                if (tile.id == other.id) continue;
                if (connection(tile, other)) {
                    if (!map.containsKey(tile)) map.put(tile, new ArrayList<>());
                    map.get(tile).add(other);
                }
            }
        }

        return map;
    }

    private static void relative(Tile a, Tile b) {
        for (var reposition : repositions()) {
            reposition.accept(b);
            if (matchRows(a, 0, b, 9)) { a.top = b; return; }
            if (matchRows(a, 9, b, 0)) { a.bottom = b; return; }
            if (matchColumns(a, 0, b, 9)) { a.left = b; return; }
            if (matchColumns(a, 9, b, 0)) { a.right = b; return; }
        }
    }

    private static boolean connection(Tile a, Tile b) {
        for (var reposition : repositions()) {
            reposition.accept(b);
            if (matchRows(a, 0, b, 9) || matchRows(a, 9, b, 0) || matchColumns(a, 0, b, 9) || matchColumns(a, 9, b, 0))
                return true;
        }
        return false;
    }

    // to iterate through all possible arrangements of a tile
    private static List<Consumer<Tile>> repositions() {
        var consumers = new ArrayList<Consumer<Tile>>();
        consumers.add(tile -> tile.rotate());
        consumers.add(tile -> tile.flip());
        consumers.add(tile -> { tile.flip(); tile.rotate(); });
        consumers.add(tile -> tile.flip());
        consumers.add(tile -> { tile.flip(); tile.rotate(); });
        consumers.add(tile -> tile.flip());
        consumers.add(tile -> { tile.flip(); tile.rotate(); });
        consumers.add(tile -> tile.flip());
        return consumers;
    }

    private static boolean matchRows(Tile a, int aRowIndex, Tile b, int bRowIndex) {
        for (var i=0; i<a.grid.length; i++) {
            if (a.grid[aRowIndex][i] != b.grid[bRowIndex][i])
                return false;
        }
        return true;
    }

    private static boolean matchColumns(Tile a, int aColIndex, Tile b, int bColIndex) {
        for (var i=0; i<a.grid.length; i++) {
            if (a.grid[i][aColIndex] != b.grid[i][bColIndex])
                return false;
        }
        return true;
    }

    private static class Tile {
        public Tile top, bottom, left, right;
        public char[][] grid; // [row][col]
        public int id;
        private final int size;

        public Tile(String input) {
            size = 10;
            grid = new char[size][size];

            var lines = input.split("\r\n");
            id = matcher.reset(lines[0]).getNextInt();
            for (var y=1; y<=size; y++) {
                for (var x=0; x<size; x++) grid[y-1][x] = lines[y].charAt(x);
            }
        }

        public Tile(char[][] data) {
            size = data.length;
            grid = data;
        }

        // 90º counterclockwise
        public void rotate() {
            var rotated = new char[size][size];
            for (var i=0; i<size; i++) rotated[size-1-i] = column(i);
            grid = rotated;
        }

        public void flip() {
            for (var i=0; i<size; i++) grid[i] = reverse(grid[i]);
        }

        private char[] reverse(char[] original) {
            var reversed = new char[original.length];
            for (var i=0; i<original.length; i++) reversed[original.length-i-1] = original[i];
            return reversed;
        }

        private char[] column(int colIndex) {
            var col = new char[grid.length];
            for (var i=0; i<grid.length; i++) col[i] = grid[i][colIndex];
            return col;
        }

        public String toString() {
            return String.valueOf(id);
        }

        public String gridToString() {
            var sb = new StringBuilder(size*(size+1));
            for (var row : grid) {
                sb.append(Utilities.arrayToString(row));
                sb.append('\n');
            }
            return sb.toString();
        }

        public String tileToString() {
            var sb = new StringBuilder((size+1)*(size+1)).append("Tile ").append(id).append(":\n");
            sb.append(gridToString());
            return sb.toString();
        }
    }
}