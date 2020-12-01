package day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var values = Files.readAllLines(Path.of("src/day1/input.txt"))
                .parallelStream().map(Integer::parseInt).collect(Collectors.toSet());

        System.out.printf("Part 1: %d\n", findAndMultiplyTwo(values));
        System.out.printf("Part 2: %d\n", findAndMultiplyThree(values));
    }

    private static int findAndMultiplyTwo(Set<Integer> set) {
        var entry = set.parallelStream().filter(value -> set.contains(2020 - value)).findAny().get();
        return entry * (2020 - entry);
    }

    private static int findAndMultiplyThree(Set<Integer> set) {
        return set.parallelStream().map(initial -> {
            var entry = set.parallelStream().filter(value -> value != initial && set.contains(2020 - initial - value)).findAny();
            return entry.isPresent() ? initial * entry.get() * (2020 - initial - entry.get()) : -1;
        }).filter(result -> result != -1).findAny().get();
    }

}