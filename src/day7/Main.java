package day7;

import utils.RegexMatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {

        var rules = parseRules(Files.readAllLines(Path.of("src/day7/input.txt")));

        System.out.printf("Part 1: %d\n", countContainedBy(rules, "shiny gold bag", null));
        System.out.printf("Part 2: %d\n", countContains(rules, "shiny gold bag"));
    }
	
	private static int countContainedBy(Map<String, Rule> rules, String bag, Set<String> results) {
        if (results == null) results = new HashSet<>();
        if (!rules.containsKey(bag)) return 0;

        for (var result : rules.get(bag).containedBy()) {
            results.add(result);
            countContainedBy(rules, result, results);
        }

        return results.size();
    }

    private static int countContains(Map<String, Rule> tree, String bag) {
        int count = 0;

        var contained = tree.get(bag).contains();
        for (var result : contained.keySet())
            count += contained.get(result) + contained.get(result) * countContains(tree, result);

        return count;
    }

    private record Rule(Set<String> containedBy, Map<String, Integer> contains) {
        public Rule() { this(new HashSet<>(), new HashMap<>()); }
    }

    private static Map<String, Rule> parseRules(List<String> writtenRules) {
        var rulePattern = Pattern.compile("\\d+|(\\w)+ (\\w)+ bag");
        var rules = new HashMap<String, Rule>();

        writtenRules.forEach(rule -> {
            var matcher = new RegexMatcher(rulePattern, rule);
            var subject = matcher.getNext();
            if (!rules.containsKey(subject)) rules.put(subject, new Rule());

            while (matcher.hasNext()) {
                var next = matcher.getNext();
                if (next.equals("no other bag")) break;

                var amount = Integer.parseInt(next);
                var bag = matcher.getNext();
                rules.get(subject).contains().put(bag, amount);

                if (!rules.containsKey(bag)) rules.put(bag, new Rule());
                rules.get(bag).containedBy().add(subject);
            }
        });

        return rules;
    }

}