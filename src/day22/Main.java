package day22;

import utils.Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) throws IOException {

        var decks = parseDecks(Files.readString(Path.of("src/day22/input.txt")));

        System.out.printf("Part 1: %d\n", playAndGetScore(decks, Main::play));
        System.out.printf("Part 2: %d\n", playAndGetScore(decks, Main::playRecursive));
    }

    private static int playAndGetScore(Decks decks, Function<Decks, Boolean> rules) {
        decks = new Decks(new ArrayDeque<>(decks.p1()), new ArrayDeque<>(decks.p2()));
        var winner = new ArrayList<>(rules.apply(decks) ? decks.p1() : decks.p2());
        var score = 0;
        for (var i=0; i<winner.size(); i++)
            score += winner.get(i) * (winner.size() - i);
        return score;
    }

    private record Decks(Queue<Integer> p1, Queue<Integer> p2) { }

    // true = p1 wins, false = p2 wins
    private static boolean play(Decks decks) {
        while (decks.p1().size() > 0 && decks.p2().size() > 0)
            turn(decks);

        return decks.p2().size() == 0;
    }

    // true = p1 wins, false = p2 wins
    private static boolean playRecursive(Decks decks) {
        var gameStates = new HashSet<String>();

        while (decks.p1().size() > 0 && decks.p2().size() > 0) {
            var state = Utilities.join(decks.p1(), ",") + ";" + Utilities.join(decks.p2(), ",");
            if (gameStates.contains(state)) return true;
            gameStates.add(state);
            turnRecursive(decks);
        }

        return decks.p2().size() == 0;
    }

    private static void turn(Decks decks) {
        var card1 = decks.p1().poll();
        var card2 = decks.p2().poll();
        turnVictory(decks, card1, card2, card1 > card2);
    }

    private static void turnRecursive(Decks decks) {
        var card1 = decks.p1().poll();
        var card2 = decks.p2().poll();
        boolean p1wins;

        if (card1 <= decks.p1().size() && card2 <= decks.p2().size())
            p1wins = playRecursive(new Decks(new ArrayDeque<>(Utilities.subQueue(decks.p1(), card1)),
                                             new ArrayDeque<>(Utilities.subQueue(decks.p2(), card2))));
        else p1wins = card1 > card2;

        turnVictory(decks, card1, card2, p1wins);
    }

    // I suppose card1 will never equal card2
    private static void turnVictory(Decks decks, Integer card1, Integer card2, boolean p1wins) {
        if (p1wins) { decks.p1().add(card1); decks.p1().add(card2); }
        else { decks.p2().add(card2); decks.p2().add(card1); }
    }

    private static Decks parseDecks(String input) {
        var players = input.split("\r\n\r\n");
        String[] entries1 = players[0].split("\r\n"), entries2 = players[1].split("\r\n");
        Queue<Integer> p1 = new ArrayDeque<>(), p2 = new ArrayDeque<>();

        for (var i=1; i<entries1.length; i++) {
            p1.add(Integer.parseInt(entries1[i]));
            p2.add(Integer.parseInt(entries2[i]));
        }

        return new Decks(p1, p2);
    }

}