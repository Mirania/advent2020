package day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        var notes = parseNotes(Files.readAllLines(Path.of("src/day13/input.txt")));

        System.out.printf("Part 1: %d\n", multNextBusId(notes));
        System.out.printf("Part 2: %d\n", magic(notes));
    }

    private static int multNextBusId(Notes notes) {
        var nextDeparture = notes.schedules().stream()
                .map(bus -> new Departure(bus, notes.estimate() % bus.id() == 0 ? notes.estimate() : (notes.estimate() / bus.id() + 1) * bus.id()))
                .min(Comparator.comparingInt(Departure::nextTimestamp)).get();
        return (nextDeparture.nextTimestamp() - notes.estimate()) * nextDeparture.bus().id();
    }

    private static long magic(Notes notes) {
        long timestamp = 0;
        long increment = notes.schedules().remove(0).id();
        for (var bus : notes.schedules()) {
            while ((timestamp + bus.offset()) % bus.id() != 0)
                timestamp += increment;
            increment = lcm(increment, bus.id());
        }
        return timestamp;
    }

    private record Bus(int id, int offset) { }

    private record Notes(int estimate, List<Bus> schedules) { }

    private record Departure(Bus bus, int nextTimestamp) { }

    private static Notes parseNotes(List<String> input) {
        var estimate = Integer.parseInt(input.get(0));
        var schedules = new ArrayList<Bus>();
        var ids = input.get(1).split(",");
        for (var i=0; i<ids.length; i++) {
            if (!ids[i].equals("x")) schedules.add(new Bus(Integer.parseInt(ids[i]), i));
        }
        return new Notes(estimate, schedules);
    }

    private static long lcm(long x, long y) {
        return x % y == 0 ? x : y % x == 0 ? y : x * y;
    }

}