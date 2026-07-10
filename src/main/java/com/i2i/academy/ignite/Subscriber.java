package com.i2i.academy.ignite;

import java.util.Objects;

/**
 * Data model representing a single mobile-network subscriber and the usage
 * accumulated on their line.
 *
 * <p>{@code customerId} is the primary key. Usage fields are mutated during the
 * simulation and then persisted back to the Ignite grid.
 */
public class Subscriber {

    private int customerId;
    private double dataUsage;   // in MB
    private int smsUsage;       // number of messages
    private int callUsage;      // in minutes

    /** Default constructor required for generic (de)serialization. */
    public Subscriber() {
    }

    public Subscriber(int customerId, double dataUsage, int smsUsage, int callUsage) {
        this.customerId = customerId;
        this.dataUsage = dataUsage;
        this.smsUsage = smsUsage;
        this.callUsage = callUsage;
    }

    /** Factory for a brand-new subscriber whose usage counters start at zero. */
    public static Subscriber baseline(int customerId) {
        return new Subscriber(customerId, 0.0, 0, 0);
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getDataUsage() {
        return dataUsage;
    }

    public void setDataUsage(double dataUsage) {
        this.dataUsage = dataUsage;
    }

    public int getSmsUsage() {
        return smsUsage;
    }

    public void setSmsUsage(int smsUsage) {
        this.smsUsage = smsUsage;
    }

    public int getCallUsage() {
        return callUsage;
    }

    public void setCallUsage(int callUsage) {
        this.callUsage = callUsage;
    }

    /** Accumulate additional data usage (MB). */
    public void addDataUsage(double amount) {
        this.dataUsage += amount;
    }

    /** Accumulate additional SMS usage (messages). */
    public void addSmsUsage(int amount) {
        this.smsUsage += amount;
    }

    /** Accumulate additional call usage (minutes). */
    public void addCallUsage(int amount) {
        this.callUsage += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subscriber)) {
            return false;
        }
        Subscriber that = (Subscriber) o;
        return customerId == that.customerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return String.format(
                "Subscriber{customerId=%d, dataUsage=%.2f MB, smsUsage=%d, callUsage=%d min}",
                customerId, dataUsage, smsUsage, callUsage);
    }
}
