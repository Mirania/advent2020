package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher {

    private Pattern pattern;
    private Matcher matcher;
    private String next;
    private int nextPosition;

    public RegexMatcher(Pattern pattern, String input) {
        this.pattern = pattern;
        this.matcher = pattern.matcher(input);
    }

    /** this forces the use of {@link RegexMatcher#reset(String)} before it can be used */
    public RegexMatcher(Pattern pattern) {
        this.pattern = pattern;
        this.matcher = pattern.matcher("");
    }

    public RegexMatcher(String regex, String input) {
        this.pattern = Pattern.compile(regex);
        this.matcher = pattern.matcher(input);
    }

    /** this forces the use of {@link RegexMatcher#reset(String)} before it can be used */
    public RegexMatcher(String regex) {
        this.pattern = Pattern.compile(regex);
        this.matcher = pattern.matcher("");
    }

    public static IntRegexMatcher intExtractor(String input) { return new IntRegexMatcher(input); }

    public static IntRegexMatcher intExtractor() { return new IntRegexMatcher(); }

    public static class IntRegexMatcher extends RegexMatcher {
        private static Pattern numberPattern = Pattern.compile("\\d+");

        public IntRegexMatcher(String input) { super(numberPattern, input); }

        /** this forces the use of {@link IntRegexMatcher#reset(String)} before it can be used */
        public IntRegexMatcher() { super(numberPattern); }

        public int getNextInt() { return Integer.parseInt(super.getNext()); }

        public IntRegexMatcher reset(String input) { return (IntRegexMatcher) super.reset(input); }
    }

    public boolean matches() {
        return matcher.matches();
    }

    public boolean hasNext() {
        if (next != null)
            return true;

        var found = matcher.find();
        if (found) {
            next = matcher.group();
            nextPosition = matcher.start();
        }

        return found;
    }

    public String getNext() {
        if (next != null) {
            var match = next;
            next = null;
            return match;
        }

        var found = matcher.find();
        return found ? matcher.group() : null;
    }

    public int getNextPosition() {
        if (next != null) {
            next = null;
            return nextPosition;
        }

        var found = matcher.find();
        return found ? matcher.start() : -1;
    }

    public List<String> getAllMatches() {
        var matches = new ArrayList<String>();
        String match;

        while ((match = getNext()) != null)
            matches.add(match);

        return matches;
    }

    public int countMatches() {
        var matches = 0;

        while (getNext() != null)
            matches++;

        return matches;
    }

    public RegexMatcher reset(String input) {
        matcher.reset(input);
        return this;
    }

}
