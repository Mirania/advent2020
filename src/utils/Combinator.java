package utils;

import java.util.ArrayList;
import java.util.List;

// adapted from https://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-in-a-given-array-of-size-n/
public class Combinator {

    /** All indexes are inclusive. */
    public static <T> List<List<T>> combinations(List<T> elements, int fromIndex, int toIndex) {
        var combinations = new ArrayList<List<T>>();
        for (var size=0; size<=toIndex-fromIndex+1; size++)
            generateCombinations(elements, combinations, fromIndex, toIndex, size);
        return combinations;
    }

    /** All indexes and sizes are inclusive. */
    public static <T> List<List<T>> combinations(List<T> elements, int fromIndex, int toIndex, int minSize, int maxSize) {
        var combinations = new ArrayList<List<T>>();
        for (var size=minSize; size<=maxSize; size++)
            generateCombinations(elements, combinations, fromIndex, toIndex, size);
        return combinations;
    }

    private static <T> void generateCombinations(List<T> elements, List<List<T>> combinations,
                                             int fromIndex, int toIndex, int comboSize) {
        var data = (T[]) new Object[comboSize];
        combinationUtil(elements, data, fromIndex, toIndex, 0, comboSize, combinations);
    }

    private static <T> void combinationUtil(List<T> elements, T[] data, int start, int end,
                                        int index, int comboSize, List<List<T>> combinations) {
        if (index == comboSize) {
            var combo = new ArrayList<T>();
            for (int j=0; j<comboSize; j++)
                combo.add(data[j]);
            combinations.add(combo);
            return;
        }

        for (int i=start; i<=end && end-i+1>=comboSize-index; i++) {
            data[index] = elements.get(i);
            combinationUtil(elements, data, i+1, end, index+1, comboSize, combinations);
        }
    }

}
