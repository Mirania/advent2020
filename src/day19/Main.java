package day19;

import utils.Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    // java lacks recursive regex support so this has to be done with trial-and-error
    private static int depth = 3;

    public static void main(String[] args) throws IOException {

        var data = parseData(Files.readString(Path.of("src/day19/input.txt")));

        System.out.printf("Part 1: %d\n", countValid(data, false));
        System.out.printf("Part 2: %d\n", countValid(data, true));
    }

    private static long countValid(Data data, boolean applyPatch) {
        if (applyPatch) {
            data.rules().put(8, "42 | 42 8");
            data.rules().put(11, "42 31 | 42 11 31");
        }

        var regex = expandRule(data.rules().get(0), data.rules(), applyPatch);
        return data.messages().stream().filter(m -> m.matches(regex)).count();
    }

    private record Data(Map<Integer, String> rules, List<String> messages) { }

    private static String expandRule(String rule, Map<Integer, String> rules, boolean patch) {
        if (patch) {
            if (rule.equals(rules.get(8))) return "(" + expandRule(rules.get(42), rules, false) + ")+";
            if (rule.equals(rules.get(11))) {
                // no recursive regex support. build this manually up to a certain recursion depth
                var opts = IntStream.rangeClosed(1, depth+1)
                        .mapToObj(i -> "42 ".repeat(i) + "31 ".repeat(i))
                        .collect(Collectors.toList());
                rule = Utilities.join(opts, "| ");
            }
        }

        if (rule.startsWith("\""))
            return rule.replaceAll("\"", "");

        if (rule.contains("|")) {
            var opts = Arrays.stream(rule.split("\\|"))
                    .map(opt -> expandRule(opt.trim(), rules, patch))
                    .collect(Collectors.toList());
            return "(" + Utilities.join(opts, "|")+ ")";
        }

        return Arrays.stream(rule.split(" "))
                .map(num -> expandRule(rules.get(Integer.parseInt(num)), rules, patch))
                .reduce("", (a,b) -> a + b);
    }

    private static Data parseData(String input) {
        var sections = input.split("\r\n\r\n");

        var rules = sections[0].lines().map(rule -> rule.split(":")).collect(Collectors.toMap(
           rule -> Integer.parseInt(rule[0]),
           rule -> rule[1].trim()
        ));

        var messages = sections[1].lines().collect(Collectors.toList());

        return new Data(rules, messages);
    }

}