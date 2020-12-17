package day14;

import utils.RegexMatcher;
import utils.RegexMatcher.IntRegexMatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    private static IntRegexMatcher matcher = RegexMatcher.intExtractor();

    public static void main(String[] args) throws IOException {

        var commands = Files.readAllLines(Path.of("src/day14/input.txt"));

        System.out.printf("Part 1: %d\n", runSimpleAndSum(commands));
        System.out.printf("Part 2: %d\n", runFloatingAndSum(commands));
    }

    private static long runSimpleAndSum(List<String> input) {
        var mem = new HashMap<Integer, Long>();
        SimpleMask mask = null;
        for (var cmd : input) {
            if (cmd.startsWith("mask")) mask = parseSimpleMask(cmd);
            else {
                var memset = parseSimpleMemset(cmd);
                mem.put(memset.position(), memset.value() & mask.zeros() | mask.ones());
            }
        }
        return mem.keySet().stream().mapToLong(mem::get).sum();
    }

    private static long runFloatingAndSum(List<String> input) {
        var mem = new HashMap<Long, Long>();
        FloatingMask mask = null;
        for (var cmd : input) {
            if (cmd.startsWith("mask")) mask = parseFloatingMask(cmd);
            else {
                var memset = parseFloatingMemset(cmd, mask);
                memset.positions().forEach(pos -> mem.put(pos, memset.value()));
            }
        }
        return mem.keySet().stream().mapToLong(mem::get).sum();
    }

    private record SimpleMask(long zeros, long ones) { }

    private record FloatingMask(String mask, long floatCount) { }

    private record SimpleMemset(int position, long value) { }

    private record FloatingMemset(List<Long> positions, long value) { }

    private static SimpleMask parseSimpleMask(String line) {
        var zeros = line.substring(7).replace('X', '1');
        var ones = line.substring(7).replace('X', '0');
        return new SimpleMask(Long.parseLong(zeros, 2), Long.parseLong(ones, 2));
    }

    private static FloatingMask parseFloatingMask(String line) {
        var mask = line.substring(7);
        var floatCount = mask.chars().filter(ch -> ch == 'X').count();
        return new FloatingMask(mask, floatCount);
    }

    private static SimpleMemset parseSimpleMemset(String line) {
        matcher.reset(line);
        return new SimpleMemset(matcher.getNextInt(), matcher.getNextInt());
    }

    private static FloatingMemset parseFloatingMemset(String line, FloatingMask mask) {
        var memset = parseSimpleMemset(line);
        var address = Integer.toBinaryString(memset.position());
        var positions = new ArrayList<Long>();
        var limit = Math.pow(2, mask.floatCount());

        for (var x=0; x<limit; x++) {
            var bin = Integer.toBinaryString(x);
            var arr = new char[36];
            var replace = bin.length()-1;
            for (var i=1; i<=arr.length; i++) {
                switch (mask.mask().charAt(mask.mask().length()-i)) {
                    case 'X' -> arr[arr.length-i] = replace < 0 ? '0' : bin.charAt(replace--);
                    case '1' -> arr[arr.length-i] = '1';
                    default -> arr[arr.length-i] = address.length()-i < 0 ? '0' : address.charAt(address.length()-i);
                }
            }
            positions.add(Long.parseUnsignedLong(new String(arr), 2));
        }
        return new FloatingMemset(positions, memset.value());
    }
}