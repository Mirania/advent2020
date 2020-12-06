package day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var groups = Arrays.asList(Files.readString(Path.of("src/day6/input.txt")).split("\r\n\r\n"));

        System.out.printf("Part 1: %d\n", sumYes(groups, Main::getAnyYesAmount));
        System.out.printf("Part 2: %d\n", sumYes(groups, Main::getAllYesAmount));
    }

    private static long sumYes(List<String> groups, ToLongFunction<String> mapper) {
        return groups.parallelStream().mapToLong(mapper).sum();
    }

    private static Long getAnyYesAmount(String answers) {
        return (long) answers.chars().boxed().filter(ch -> ch != '\r' && ch != '\n').collect(Collectors.toSet()).size();
    }

    private static Long getAllYesAmount(String answers) {
        var charMap = new HashMap<Character, Integer>();
        answers.chars().forEach(ch -> charMap.merge((char) ch, 1, Integer::sum));
        return charMap.values().stream().filter(amount -> amount == charMap.getOrDefault('\n', 0) + 1).count();
    }

}