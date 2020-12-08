package day8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static int accumulator;

    public static void main(String[] args) throws IOException {

        var tape = Files.readAllLines(Path.of("src/day8/input.txt")).stream()
                .map(Main::parseInstruction).collect(Collectors.toList());

        System.out.printf("Part 1: %d\n", runUntilLoop(tape));
        System.out.printf("Part 2: %d\n", fixAndRun(tape));
    }

    private static int runUntilLoop(List<Instruction> tape) {
        int pointer = 0; accumulator = 0;
        while (!tape.get(pointer).executed)
            pointer = run(tape.get(pointer), pointer);
        return accumulator;
    }

    private static Integer fixAndRun(List<Instruction> tape) {
        var fixes = new ArrayList<Integer>();
        for (var i=0; i<tape.size(); i++)
            if (tape.get(i).op.equals("jmp") || tape.get(i).op.equals("nop"))
                fixes.add(i);

        for (var i=0; i<fixes.size(); i++) {
            tape.get(fixes.get(i)).op = tape.get(fixes.get(i)).op.equals("jmp") ? "nop" : "jmp";
            if (i>0) tape.get(fixes.get(i-1)).op = tape.get(fixes.get(i-1)).op.equals("jmp") ? "nop" : "jmp";

            int pointer = 0; accumulator = 0;
            while (!tape.get(pointer).executed) {
                pointer = run(tape.get(pointer), pointer);
                if (pointer == tape.size())
                    return accumulator;
            }

            reset(tape);
        }

        return null;
    }

    private static class Instruction {
        public String op; public int arg; public boolean executed;
        public Instruction(String op, int arg) { this.op = op; this.arg = arg; }
    }

    private static Instruction parseInstruction(String instruction) {
        var values = instruction.split(" ");
        return new Instruction(values[0], Integer.parseInt(values[1]));
    }

    private static int run(Instruction instruction, int pointer) {
        instruction.executed = true;
        return switch (instruction.op) {
            case "acc" -> { accumulator += instruction.arg; yield pointer + 1; }
            case "jmp" -> pointer + instruction.arg;
            default -> pointer + 1;
        };
    }

    private static void reset(List<Instruction> tape) {
        tape.forEach(instruction -> instruction.executed = false);
    }

}