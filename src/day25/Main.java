package day25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        //index 0 = card, index 1 = door
        var publicKeys = Files.readAllLines(Path.of("src/day25/input.txt")).stream()
                .map(Integer::parseInt).collect(Collectors.toList());

        System.out.printf("Final: %d\n", bruteforce(publicKeys));
    }

    private static long bruteforce(List<Integer> publicKeys) {
        long doorValue = 1, doorLoopSize = 0;
        do { doorValue = loop(doorValue, 7); doorLoopSize++; } while (doorValue != publicKeys.get(1));

        long encryptionKey = 1;
        for (var i=0; i<doorLoopSize; i++)
            encryptionKey = loop(encryptionKey, publicKeys.get(0));
        return encryptionKey;
    }

    private static long loop(long prevValue, int subject) {
        return (prevValue * subject) % 20201227;
    }

}