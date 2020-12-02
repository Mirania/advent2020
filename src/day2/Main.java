package day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var entries = Files.readAllLines(Path.of("src/day2/input.txt"))
                .parallelStream().map(line -> {
                    var data = line.split("-| |: ");
                    return new Entry(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2].charAt(0), data[3]);
                }).collect(Collectors.toList());

        System.out.printf("Part 1: %d\n", countValidPasswordsRange(entries));
        System.out.printf("Part 2: %d\n", countValidPasswordsPosition(entries));
    }

    private record Entry(int lowValue, int highValue, char letter, String password) { }

    private static long countValidPasswordsRange(List<Entry> entries) {
        return entries.parallelStream().filter(entry -> {
           var count = entry.password().chars().filter(ch -> ch == entry.letter()).count();
           return count >= entry.lowValue() && count <= entry.highValue();
        }).count();
    }

    private static long countValidPasswordsPosition(List<Entry> entries) {
        return entries.parallelStream().filter(entry ->
            (entry.password().charAt(entry.lowValue() - 1) == entry.letter()) != (entry.password().charAt(entry.highValue() - 1) == entry.letter())
        ).count();
    }

}