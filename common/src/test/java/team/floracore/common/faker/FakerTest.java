package team.floracore.common.faker;

import com.github.javafaker.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.stream.*;

public class FakerTest {
    private static final Faker faker = new Faker();

    private static String getRandomReplacement() {
        double randomNum = Math.random();
        if (randomNum < 0.3) {
            return "";
        } else if (randomNum < 0.6) {
            return "_";
        } else {
            return getRandomDigitOrLetter();
        }
    }

    private static String getRandomDigitOrLetter() {
        double randomNum = Math.random();
        if (randomNum < 0.5) {
            return Character.toString((char) (Math.random() * 26 + 'a'));
        } else {
            return Integer.toString((int) (Math.random() * 10));
        }
    }

    @Test
    public void onFaker() {
        String randomName;
        int randomNum = ThreadLocalRandom.current().nextInt(4);
        int dr = ThreadLocalRandom.current().nextInt(5);
        int dei = ThreadLocalRandom.current().nextInt(10) + 4;
        String digit = IntStream.range(0, dr)
                .mapToObj(i -> String.valueOf(ThreadLocalRandom.current().nextInt(10)))
                .collect(Collectors.joining());
        String digit1 = IntStream.range(0, dei)
                .mapToObj(i -> String.valueOf(ThreadLocalRandom.current().nextInt(10)))
                .collect(Collectors.joining());
        String[] randomNameOptions = {
                faker.name().lastName() + "_" + digit,
                digit + "_" + faker.name().firstName(),
                digit1,
                faker.name().fullName()
        };
        randomName = randomNameOptions[randomNum].replaceAll("\\s+|\\.", getRandomReplacement());
        System.out.println(randomName);
    }
}
