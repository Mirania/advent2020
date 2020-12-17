package day4;

import utils.RegexMatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Main {

    private static Pattern fieldsPattern = Pattern.compile("byr:|iyr:|eyr:|hgt:|hcl:|ecl:|pid:");

    public static void main(String[] args) throws IOException {

        var passports = Arrays.asList(Files.readString(Path.of("src/day4/input.txt")).split("\r\n\r\n"));

        System.out.printf("Part 1: %d\n", countWithValidKeys(passports));
        System.out.printf("Part 2: %d\n", countWithValidKeysAndValues(passports));
    }

    private static long countWithValidKeys(List<String> passports) {
        return passports.parallelStream().filter(passport -> hasAllFields(passport)).count();
    }

    private static long countWithValidKeysAndValues(List<String> passports) {
        return passports.parallelStream().filter(passport -> hasAllFields(passport) && hasValidFields(passport)).count();
    }

    private static boolean hasAllFields(String passport) {
        var matcher = new RegexMatcher(fieldsPattern, passport); var fields = 0;
        while (matcher.getNext() != null) fields++;
        return fields == 7;
    }

    private static boolean hasValidFields(String passport) {
        var data = passport.split(":| |\r\n");
        for (var i=0; i<data.length; i+=2)
            if (!isValidField(data[i], data[i+1])) return false;
        return true;
    }

    private static boolean isValidField(String key, String value) {
        return switch (key) {
            case "byr" -> { var year = Integer.parseInt(value); yield year >= 1920 && year <= 2002; }
            case "iyr" -> { var year = Integer.parseInt(value); yield year >= 2010 && year <= 2020; }
            case "eyr" -> { var year = Integer.parseInt(value); yield year >= 2020 && year <= 2030; }
            case "hgt" -> {
                var height = RegexMatcher.intExtractor(value).getNextInt();
                yield value.endsWith("cm") ? height >= 150 && height <= 193 : value.endsWith("in") ? height >= 59 && height <= 76 : false;
            }
            case "hcl" -> value.matches("#[0-9a-f]{6}");
            case "ecl" -> value.matches("amb|blu|brn|gry|grn|hzl|oth");
            case "pid" -> value.matches("[0-9]{9}");
            default -> true;
        };
    }

}