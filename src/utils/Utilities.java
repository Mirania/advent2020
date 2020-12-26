package utils;

import java.util.*;
import java.util.stream.Collectors;

public class Utilities {

    public static <T> String join(T[] objects, String separator) {
        var joined = "";
        for (var i = 0; i < objects.length; i++)
            joined += objects[i].toString() + (i < objects.length - 1 ? separator : "");
        return joined;
    }

    public static String join(Collection<?> objects, String separator) {
        return join(objects.toArray(), separator);
    }

    public static String arrayToString(char[] array) {
        var sb = new StringBuilder(array.length);
        for (var ch : array)
            sb.append(ch);
        return sb.toString();
    }

    public static <T> List<T> intersect(Collection<T> a, Collection<T> b) {
        return a.parallelStream().filter(b::contains).collect(Collectors.toList());
    }

    public static <T> Queue<T> subQueue(Queue<T> source, int size) {
        var subQueue = new ArrayDeque<T>();
        var elements = 0;
        for (var element : source) {
            subQueue.add(element);
            if (++elements == size) break;
        }
        return subQueue;
    }

}
