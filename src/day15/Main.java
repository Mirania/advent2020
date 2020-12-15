package day15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var input = Files.readString(Path.of("src/day15/input.txt"));

        System.out.printf("Part 1: %d\n", say(new ArrayGame(input, 2020), 2020));
        System.out.printf("Part 2: %d\n", say(new ArrayGame(input, 30_000_000), 30_000_000));

        //System.out.printf("Part 1: %d\n", say(new MapGame(input), 2020));
        //System.out.printf("Part 2: %d\n", say(new MapGame(input), 30_000_000));
    }

    private static int say(Game game, int finalTurn) {
        for (var i=game.getTurnNumber()+1; i<=finalTurn; i++)
            game.turn();
        return game.getSaidNumber();
    }

    private interface Game {
        void turn();
        int getTurnNumber();
        int getSaidNumber();
    }

    // faster impl (pre-sized array)

    private static class ArrayGame implements Game {
        private int[] memory;
        private int said;
        private int turn;

        public ArrayGame(String input, int maxTurn) {
            var starting = Arrays.stream(input.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            memory = new int[maxTurn];
            for (var i=0; i<starting.size()-1; i++)
                memory[starting.get(i)] = i+1;
            said = starting.get(starting.size()-1);
            turn = starting.size();
        }

        public void turn() {
            var lastSaidTurn = memory[said];
            memory[said] = turn;
            said = lastSaidTurn == 0 ? 0 : turn - lastSaidTurn;
            turn++;
        }

        public int getTurnNumber() { return turn; }

        public int getSaidNumber() { return said; }
    }

    // slower impl (map)

    private static class MapGame implements Game {
        private Map<Integer, Integer> memory;
        private int said;
        private int turn;

        public MapGame(String input) {
            var starting = Arrays.stream(input.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            memory = new HashMap<>();
            for (var i=0; i<starting.size()-1; i++)
                memory.put(starting.get(i), i+1);
            said = starting.get(starting.size()-1);
            turn = starting.size();
        }

        public void turn() {
            var lastSaidTurn = memory.get(said);
            memory.put(said, turn);
            said = lastSaidTurn == null ? 0 : turn - lastSaidTurn;
            turn++;
        }

        public int getTurnNumber() { return turn; }

        public int getSaidNumber() { return said; }
    }

}