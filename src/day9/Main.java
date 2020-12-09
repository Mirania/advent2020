package day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var values = Files.readAllLines(Path.of("src/day9/input.txt")).stream()
                .map(Long::parseLong).collect(Collectors.toList());

        System.out.printf("Part 1: %d\n", findBadIndex(values, 25));
        System.out.printf("Part 2: %d\n", findWeakness(values, findBadIndex(values, 25)));
    }

    private static Long findWeakness(List<Long> input, int badIndex) {
        var sums = new HashMap<Integer, Long>();
        var values = input.subList(0, badIndex+1);
        for (var pairSize=2; pairSize<=values.size(); pairSize++) {
            for (var i=0; i<=values.size()-pairSize; i++) {
                var sum = sums.containsKey(i) ? sums.get(i) + values.get(i+pairSize-1) : values.get(i)+values.get(i+1);
                if (sum == values.get(badIndex)) {
                    var contiguous = values.subList(i, i+pairSize);
                    Collections.sort(contiguous);
                    return contiguous.get(0) + contiguous.get(contiguous.size()-1);
                }
                sums.put(i, sum);
            }
        }
        return null;
    }

    private static int findBadIndex(List<Long> input, int preambleSize) {
        var cypher = new Cypher(input, preambleSize);
        while (cypher.readNext()) { }
        return cypher.index;
    }

    private static class Cypher {
        private Map<Long, Long> storage;
        private List<Long> values;
        private long oldest;
        private long newest;

        public int index;

        public Cypher(List<Long> input, int preambleSize) {
            storage = new HashMap<>();
            values = input;
            oldest = values.get(0);
            newest = values.get(preambleSize-1);
            index = preambleSize;
            for (var i=0; i<preambleSize; i++)
                storage.put(values.get(i), i+1 >= preambleSize ? null : values.get(i+1));
        }

        public boolean readNext() {
            var target = values.get(index);
            for (var key : storage.keySet()) {
                if (target - key != key && storage.containsKey(target - key)) {
                    var toRemove = oldest;
                    oldest = storage.get(oldest);
                    storage.remove(toRemove);
                    storage.put(newest, target);
                    storage.put(target, null);
                    newest = target;
                    index++;
                    return true;
                }
            }
            return false;
        }
    }

}