package day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        //x,y begin at (0,0). positive x goes right, positive y goes down. to read a point, grid.get(y).get(x)
        var grid = Files.readAllLines(Path.of("src/day3/input.txt"))
                .stream().map(line -> line.chars().mapToObj(ch -> ch == '#').collect(Collectors.toList()))
                .collect(Collectors.toList());

        System.out.printf("Part 1: %d\n", slope(grid, 3, 1));
        System.out.printf("Part 2: %d\n", slope(grid, 1, 1) * slope(grid, 3, 1) * slope(grid, 5, 1) * slope(grid, 7, 1) * slope(grid, 1, 2));
    }

    private static long slope(List<List<Boolean>> grid, int rightSteps, int downSteps) {
        int x = 0, y = 0, rows = grid.size(), columns = grid.get(0).size(), trees = 0;
        while (y + downSteps < rows)
            trees += grid.get(y = y + downSteps).get(x = (x + rightSteps) % columns) ? 1 : 0;
        return trees;
    }

}