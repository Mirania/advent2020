package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher {

    private Pattern pattern;
    private Matcher matcher;
    private String input;
    private String next;

    public RegexMatcher(Pattern pattern, String input) {
        this.pattern = pattern;
        this.matcher = pattern.matcher(input);
        this.input = input;
    }

    public RegexMatcher(String regex, String input) {
        this.pattern = Pattern.compile(regex);
        this.matcher = pattern.matcher(input);
        this.input = input;
    }

    public boolean matches() {
        return matcher.matches();
    }

    public boolean hasNext() {
        if (next != null)
            return true;

        var found = matcher.find();
        if (found)
            next = matcher.group();

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

    public List<String> getAllMatches() {
        var matches = new ArrayList<String>();
        String match;

        while ((match = getNext()) != null)
            matches.add(match);

        return matches;
    }

    public void reset() {
        this.matcher = pattern.matcher(input);
    }

}
