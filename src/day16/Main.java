package day16;

import utils.RegexMatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws IOException {
        var notes = parseNotes(Files.readString(Path.of("src/day16/input.txt")));

        System.out.printf("Part 1: %d\n", calculateErrorRate(notes));
        System.out.printf("Part 2: %d\n", mapRulesToFieldsAndMultiply(notes));
    }

    private static int calculateErrorRate(Notes notes) {
        return notes.tickets().parallelStream().mapToInt(ticket ->
            ticket.stream().filter(num -> notes.rules().stream().noneMatch(rule -> fitsRule(num, rule))).reduce(0, Integer::sum)
        ).sum();
    }

    private static long mapRulesToFieldsAndMultiply(Notes notes) {
        var validTickets = discardInvalidTickets(notes);
        var titlesPerColumn = IntStream.range(0, notes.rules().size()).boxed().collect(Collectors.toMap(
                col -> col,
                col -> notes.rules().stream()
                        .filter(rule -> validTickets.parallelStream().allMatch(ticket -> fitsRule(ticket.get(col), rule)))
                        .collect(Collectors.toList())
        ));

        var titles = new ArrayList<>(titlesPerColumn.entrySet());
        Collections.sort(titles, Comparator.comparingInt(key -> key.getValue().size()));
        for (var i=0; i<titles.size(); i++) {
            var title = titles.get(i).getValue().get(0);
            for (var x=i+1; x<titles.size(); x++) {
                titles.get(x).getValue().remove(title);
            }
        }

        return titles.stream().mapToLong(entry ->
            entry.getValue().get(0).field().startsWith("departure") ? notes.myTicket().get(entry.getKey()) : 1
        ).reduce(1, (a,b) -> a * b);
    }

    private record Rule(String field, int lowMin, int lowMax, int highMin, int highMax) { }

    private record Notes(List<Rule> rules, List<Integer> myTicket, List<List<Integer>> tickets) { }

    private static List<List<Integer>> discardInvalidTickets(Notes notes) {
        return notes.tickets().parallelStream().filter(ticket ->
                ticket.stream().allMatch(num -> notes.rules().stream().anyMatch(rule -> fitsRule(num, rule)))
        ).collect(Collectors.toList());
    }

    private static boolean fitsRule(int value, Rule rule) {
        return (value >= rule.lowMin() && value <= rule.lowMax()) || (value >= rule.highMin() && value <= rule.highMax());
    }

    private static Notes parseNotes(String input) {
        var sections = input.split("\r\n\r\n");

        var matcher = RegexMatcher.intExtractor();
        var rules = sections[0].lines().map(rule -> {
            matcher.reset(rule);
            return new Rule(rule.split(":")[0], matcher.getNextInt(), matcher.getNextInt(), matcher.getNextInt(), matcher.getNextInt());
        }).collect(Collectors.toList());

        var myTicket = Arrays.stream(sections[1].split("\r\n")[1].split(","))
                .map(Integer::parseInt).collect(Collectors.toList());

        var tickets = sections[2].lines()
                .filter(t -> !t.startsWith("n")).map(nums ->
                        Arrays.asList(nums.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList())
                ).collect(Collectors.toList());

        return new Notes(rules, myTicket, tickets);
    }

}