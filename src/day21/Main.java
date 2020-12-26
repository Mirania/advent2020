package day21;

import utils.RegexMatcher;
import utils.Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var list = parseIngredientList(Files.readAllLines(Path.of("src/day21/input.txt")));

        System.out.printf("Part 1: %d\n", countNonAllergens(list));
        System.out.printf("Part 2: %s\n", buildDangerousList(list));
    }

    private static int countNonAllergens(IngredientList list) {
        return list.entries().parallelStream()
                .map(entry -> entry.ingredients().parallelStream()
                        .filter(i -> !list.translations().containsKey(i))
                        .collect(Collectors.toList()))
                .mapToInt(List::size)
                .sum();
    }

    private static String buildDangerousList(IngredientList list) {
        var allergens = new ArrayList<>(list.translations().keySet());
        Collections.sort(allergens, Comparator.comparing(a -> list.translations().get(a)));
        return Utilities.join(allergens, ",");
    }

    private record Entry(List<String> ingredients, List<String> allergens) { }

    private record IngredientList(List<Entry> entries, Map<String, String> translations) { }

    private static Map<String, String> getTranslations(List<Entry> entries) {
        var allergens = getAllergens(entries);
        var translationCandidates = new HashMap<String, List<String>>();

        for (var allergen : allergens) {
            List<String> candidates = null;
            for (var entry : entries) {
                if (entry.allergens().contains(allergen))
                    candidates = candidates == null ? entry.ingredients() : Utilities.intersect(candidates, entry.ingredients());
            }
            translationCandidates.put(allergen, candidates);
        }

        var results = new ArrayList<>(translationCandidates.entrySet());
        Collections.sort(results, Comparator.comparingInt(a -> a.getValue().size()));

        var translations = new HashMap<String, String>();
        while (translations.size() < results.size()) {
            for (var result : results) {
                if (result.getValue().size() == 1) {
                    var translation = result.getValue().get(0);
                    translations.put(translation, result.getKey());
                    for (var entry : results)
                        entry.getValue().remove(translation);
                    break;
                }
            }
        }

        return translations;
    }

    private static Set<String> getAllergens(List<Entry> entries) {
        return entries.parallelStream().flatMap(entry -> entry.allergens().stream()).collect(Collectors.toSet());
    }

    private static IngredientList parseIngredientList(List<String> lines) {
        var wordMatcher = new RegexMatcher("\\b(\\w+)\\b");

        var entries = lines.stream().map(line -> {
            var subLists = line.split("\\(contains");
            var ingredients = wordMatcher.reset(subLists[0]).getAllMatches();
            var allergens = wordMatcher.reset(subLists[1]).getAllMatches();
            return new Entry(ingredients, allergens);
        }).collect(Collectors.toList());

        return new IngredientList(entries, getTranslations(entries));
    }
}