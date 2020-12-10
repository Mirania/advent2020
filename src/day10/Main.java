package day10;

import utils.Combinator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var values = initList(Files.readAllLines(Path.of("src/day10/input.txt")).stream()
                .map(Integer::parseInt).collect(Collectors.toList()));

        System.out.printf("Part 1: %d\n", calcJoltDifferences(values));
        System.out.printf("Part 2: %d\n", calcAdapterCombinations(values));
    }

    private static int calcJoltDifferences(List<Integer> adapters) {
        var diffs = new HashMap<Integer, Integer>();
        for (var i=1; i<adapters.size(); i++)
            diffs.merge(adapters.get(i) - adapters.get(i-1), 1, Integer::sum);
        return diffs.get(1) * diffs.get(3);
    }

    private static long calcAdapterCombinations(List<Integer> adapters) {
        var mandatory = new boolean[adapters.size()];
        for (var i=0; i<adapters.size(); i++) {
            mandatory[i] = i == 0 || i == adapters.size()-1 ||
                    adapters.get(i) - adapters.get(i-1) == 3 || adapters.get(i+1) - adapters.get(i) == 3;
        }

        int optStart = -1, optEnd = -1;
        long combinations = 1;
        for (var i=0; i<adapters.size(); i++) {
            if (mandatory[i]) {
                if (optStart != -1 && optEnd != -1)
                    combinations *= testSubList(adapters, optStart, optEnd);
                optStart = -1;
                optEnd = -1;
            } else {
                optStart = optStart == -1 ? i : optStart;
                optEnd = i;
            }
        }

        return combinations;
    }

    private static List<Integer> initList(List<Integer> baseList) {
        baseList.add(0);
        Collections.sort(baseList);
        baseList.add(baseList.get(baseList.size()-1) + 3);
        return baseList;
    }

    // indexes both inclusive
    private static int testSubList(List<Integer> list, int fromIndex, int toIndex) {
        var validCount = 0;

        for (var combo : Combinator.combinations(list, fromIndex, toIndex)) {
            var valid = true;

            for (var i=0; i<=combo.size(); i++) {
                var cur = i == combo.size() ? list.get(toIndex+1) : combo.get(i);
                var prev = i == 0 ? list.get(fromIndex-1) : combo.get(i-1);
                if (cur - prev > 3) {
                    valid = false;
                    break;
                }
            }

            if (valid) validCount++;
        }

        return validCount;
    }

}