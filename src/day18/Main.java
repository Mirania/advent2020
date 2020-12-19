package day18;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) throws IOException {

        var input = Files.readAllLines(Path.of("src/day18/input.txt"));

        System.out.printf("Part 1: %d\n", solveAndSum(input));
        System.out.printf("Part 2: %d\n", solveAdvancedAndSum(input));
    }

    private static long solveAndSum(List<String> input) {
        return input.stream().mapToLong(expr -> solve(expr, Main::solveNaive)).sum();
    }

    private static long solveAdvancedAndSum(List<String> input) {
        return input.stream().mapToLong(expr -> solve(expr, Main::solveWithSumPrecedence)).sum();
    }

    private static long solve(String expr, Function<String, Long> simpleExprSolver) {
        var stack = new ArrayDeque<Integer>();
        var parensExprs = new ArrayList<String>();
        var currentExpr = expr;
        for (var i=0; i<currentExpr.length(); i++) {
            if (currentExpr.charAt(i) == '(') stack.push(i);
            else if (currentExpr.charAt(i) == ')') parensExprs.add(currentExpr.substring(stack.pop(), i+1));
        }
        while (parensExprs.size() > 0) {
            var parensExpr = parensExprs.remove(0);
            var value = simpleExprSolver.apply(parensExpr.replaceAll("\\(|\\)", ""));
            currentExpr = currentExpr.replace(parensExpr, String.valueOf(value));
            for (var i=0; i<parensExprs.size(); i++)
                parensExprs.set(i, parensExprs.get(i).replace(parensExpr, String.valueOf(value)));
        }

        return simpleExprSolver.apply(currentExpr);
    }

    private static long solveWithSumPrecedence(String simpleExpr) {
        while (simpleExpr.contains("+")) {
            var tokens = simpleExpr.split(" ");
            for (var i=0; i<tokens.length; i++) {
                if (tokens[i].equals("+")) {
                    var value = Long.parseLong(tokens[i-1]) + Long.parseLong(tokens[i+1]);
                    simpleExpr = simpleExpr.replaceAll("\\b"+tokens[i-1]+" \\+ "+tokens[i+1]+"\\b", String.valueOf(value));
                    break;
                }
            }
        }

        return solveNaive(simpleExpr);
    }

    private static long solveNaive(String simpleExpr) {
        var tokens = simpleExpr.split(" ");
        var prev = Long.parseLong(tokens[0]); var op = "";
        for (var i=1; i<tokens.length; i++) {
            if (i % 2 == 1) op = tokens[i];
            else {
                var next = Long.parseLong(tokens[i]);
                prev = op.equals("+") ? prev + next : op.equals("*") ? prev * next : prev;
            }
        }

        return prev;
    }
}