package com.i2i.academy.ignite;

import java.util.List;
import java.util.Random;

import org.apache.ignite.client.IgniteClient;

/**
 * Entry point for the Apache Ignite 3 subscriber simulation.
 *
 * <p>The program:
 * <ol>
 *   <li>connects to the local Ignite node with the thin client;</li>
 *   <li>creates the {@code Subscriber} table and clears it for a fresh run;</li>
 *   <li>inserts five baseline subscribers with zeroed usage;</li>
 *   <li>simulates random data / SMS / call usage and updates each record;</li>
 *   <li>prints the final state of all subscribers.</li>
 * </ol>
 */
public final class App {

    private static final int SUBSCRIBER_COUNT = 5;
    private static final double MAX_DATA_USAGE_MB = 1024.0; // up to ~1 GB per run
    private static final int MAX_SMS_USAGE = 100;
    private static final int MAX_CALL_USAGE_MIN = 300;

    private static final Random RANDOM = new Random();

    private App() {
    }

    public static void main(String[] args) {
        try (IgniteClient client = IgniteConnection.open()) {
            System.out.println("Connected to Ignite node(s): " + client.connections());

            SubscriberRepository repository = new SubscriberRepository(client);

            repository.createTableIfAbsent();
            repository.clear();
            System.out.println("Subscriber table is ready and cleared.");

            insertBaselineSubscribers(repository);
            simulateUsage(repository);
            printFinalState(repository);
        }
    }

    /** Inserts {@value #SUBSCRIBER_COUNT} subscribers with zeroed usage. */
    private static void insertBaselineSubscribers(SubscriberRepository repository) {
        for (int customerId = 1; customerId <= SUBSCRIBER_COUNT; customerId++) {
            repository.insert(Subscriber.baseline(customerId));
        }
        System.out.println("Inserted " + SUBSCRIBER_COUNT + " baseline subscribers.");
    }

    /**
     * Reads each subscriber back from the grid, adds random usage, and writes the
     * updated record back to Ignite.
     */
    private static void simulateUsage(SubscriberRepository repository) {
        for (Subscriber subscriber : repository.findAll()) {
            subscriber.addDataUsage(round(RANDOM.nextDouble() * MAX_DATA_USAGE_MB));
            subscriber.addSmsUsage(RANDOM.nextInt(MAX_SMS_USAGE));
            subscriber.addCallUsage(RANDOM.nextInt(MAX_CALL_USAGE_MIN));
            repository.update(subscriber);
        }
        System.out.println("Simulated random usage and updated all subscribers.");
    }

    /** Prints the final, persisted state of every subscriber. */
    private static void printFinalState(SubscriberRepository repository) {
        List<Subscriber> subscribers = repository.findAll();
        System.out.println("\n----- Final Subscriber State -----");
        subscribers.forEach(System.out::println);
        System.out.println("----------------------------------");
    }

    /** Rounds a value to two decimal places for readable data-usage figures. */
    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
