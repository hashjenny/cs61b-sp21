package test;

import capers.CapersRepository;
import capers.Dog;
import org.junit.*;

import java.io.IOException;

public class DogTest {
    @Test
    public void CreateDogTest() throws IOException {
        CapersRepository.setupPersistence();

//        Dog dog = new Dog("Mammoth",  "German Spitz" ,10);
        CapersRepository.makeDog("Mammoth",  "German Spitz" ,10);
    }

}
