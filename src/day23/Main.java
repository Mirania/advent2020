package day23;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {

        var input = Files.readString(Path.of("src/day23/input.txt"));

        System.out.printf("Part 1: %s\n", simulate(input));
        System.out.printf("Part 2: %d\n", simulateMore(input));
    }

    private static String simulate(String input) {
        var game = new Game(input, false);
        for (var i=0; i<100; i++)
            game.move();

        var current = game.refs[0];
        var sb = new StringBuilder(game.size - 1);
        for (var i=1; i<game.size; i++) {
            current = current.next;
            sb.append(current.label);
        }

        return sb.toString();
    }

    private static long simulateMore(String input) {
        var game = new Game(input, true);
        for (var i=0; i<10_000_000; i++)
            game.move();

        return (long) game.refs[0].next.label * game.refs[0].next.next.label;
    }

    private static class Cup {
        public int label; public Cup next;
        public Cup(int label) { this.label = label; }
    }

    private static class Game {
        public Cup head; // to iterate and modify cup order
        public Cup[] refs; // to access the cups by label (index = label-1)
        private final int size;

        public Game(String input, boolean expand) {
            size = expand ? 1_000_000 : 9;
            refs = new Cup[size];

            Cup prev = null;
            for (var i=0; i<size; i++) {
                var cup = new Cup(i >= 9 ? i+1 : Integer.parseInt(input.substring(i, i+1)));
                if (i == 0) head = cup;
                refs[cup.label - 1] = cup;
                if (prev != null) prev.next = cup;
                if (i == size - 1) cup.next = head;
                else prev = cup;
            }
        }

        public void move() {
            var pickedUp = head.next;
            head.next = pickedUp.next.next.next;

            var dest = refs[destination(head, pickedUp) - 1];
            pickedUp.next.next.next = dest.next;
            dest.next = pickedUp;
            head = head.next;
        }

        private int destination(Cup head, Cup pickedUp) {
            var d = head.label - 1;
            while (d == pickedUp.label || d == pickedUp.next.label || d == pickedUp.next.next.label || d < 1)
                d = d < 1 ? size : d - 1;
            return d;
        }
    }

}