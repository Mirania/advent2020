package day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var passIds = Files.readAllLines(Path.of("src/day5/input.txt"))
                .parallelStream().map(Main::decodePass).collect(Collectors.toList());

        System.out.printf("Part 1: %d\n", getHighestSeatId(passIds));
        System.out.printf("Part 2: %d\n", getMissingSeatId(passIds));
    }

    private static int getHighestSeatId(List<Integer> passIds) {
        return passIds.parallelStream().max(Integer::compareTo).get();
    }

    private static int getMissingSeatId(List<Integer> passIds) {
        Collections.sort(passIds);
        for (int i=1, last=passIds.get(0); i<passIds.size(); i++) {
            if (passIds.get(i) != last + 1) return last + 1;
            last = passIds.get(i);
        }
        return -1;
    }

    private static int decodePass(String pass) {
        int minRow = 0, maxRow = 127, minCol = 0, maxCol = 7;
        for (var i=0; i<pass.length(); i++) {
            switch (pass.charAt(i)) {
                case 'F' -> maxRow = (minRow + maxRow) / 2;
                case 'B' -> minRow = (minRow + maxRow) / 2 + 1;
                case 'L' -> maxCol = (minCol + maxCol) / 2;
                default -> minCol = (minCol + maxCol) / 2 + 1;
            }
        }
        return maxRow * 8 + maxCol;
    }

}