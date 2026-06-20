package pl.edu.bikerental.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CustomerProfile getCustomerProfileSample1() {
        return new CustomerProfile().id(1L).address("address1").city("city1").loyaltyPoints(1);
    }

    public static CustomerProfile getCustomerProfileSample2() {
        return new CustomerProfile().id(2L).address("address2").city("city2").loyaltyPoints(2);
    }

    public static CustomerProfile getCustomerProfileRandomSampleGenerator() {
        return new CustomerProfile()
            .id(longCount.incrementAndGet())
            .address(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .loyaltyPoints(intCount.incrementAndGet());
    }
}
