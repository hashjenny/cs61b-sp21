package test;

import capers.CapersRepository;
import org.junit.*;

import java.io.IOException;

public class DogTest {
    @Test
    public void createDogTest() throws IOException {
        CapersRepository.setupPersistence();

        CapersRepository.makeDog("Mammoth", "German Spitz", 10);
    }
}
