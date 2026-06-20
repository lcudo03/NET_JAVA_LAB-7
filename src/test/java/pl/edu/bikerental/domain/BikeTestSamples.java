package pl.edu.bikerental.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BikeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Bike getBikeSample1() {
        return new Bike().id(1L).name("name1").serialNumber("serialNumber1");
    }

    public static Bike getBikeSample2() {
        return new Bike().id(2L).name("name2").serialNumber("serialNumber2");
    }

    public static Bike getBikeRandomSampleGenerator() {
        return new Bike().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).serialNumber(UUID.randomUUID().toString());
    }
}
