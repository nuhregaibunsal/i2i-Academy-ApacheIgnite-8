package com.i2i.academy.ignite;

import java.util.ArrayList;
import java.util.List;

import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.sql.ResultSet;
import org.apache.ignite.sql.SqlRow;

/**
 * Encapsulates all persistence concerns for {@link Subscriber} records against
 * the Apache Ignite 3 grid. The table is accessed through Ignite SQL so the
 * behaviour maps cleanly onto standard DDL/DML statements.
 */
public class SubscriberRepository {

    private final IgniteClient client;

    public SubscriberRepository(IgniteClient client) {
        this.client = client;
    }

    /** Creates the {@code Subscriber} table if it does not already exist. */
    public void createTableIfAbsent() {
        client.sql().execute(null,
                "CREATE TABLE IF NOT EXISTS Subscriber ("
                        + "customer_id INT PRIMARY KEY, "
                        + "data_usage DOUBLE, "
                        + "sms_usage INT, "
                        + "call_usage INT)");
    }

    /**
     * Empties the table so every program run starts from a clean state.
     * {@code DELETE} is used (rather than dropping the table) to keep the schema
     * intact between runs.
     */
    public void clear() {
        client.sql().execute(null, "DELETE FROM Subscriber");
    }

    /** Inserts a single subscriber. */
    public void insert(Subscriber subscriber) {
        client.sql().execute(null,
                "INSERT INTO Subscriber (customer_id, data_usage, sms_usage, call_usage) "
                        + "VALUES (?, ?, ?, ?)",
                subscriber.getCustomerId(),
                subscriber.getDataUsage(),
                subscriber.getSmsUsage(),
                subscriber.getCallUsage());
    }

    /** Persists the current usage values of an existing subscriber. */
    public void update(Subscriber subscriber) {
        client.sql().execute(null,
                "UPDATE Subscriber SET data_usage = ?, sms_usage = ?, call_usage = ? "
                        + "WHERE customer_id = ?",
                subscriber.getDataUsage(),
                subscriber.getSmsUsage(),
                subscriber.getCallUsage(),
                subscriber.getCustomerId());
    }

    /** Returns every subscriber, ordered by primary key. */
    public List<Subscriber> findAll() {
        List<Subscriber> subscribers = new ArrayList<>();
        try (ResultSet<SqlRow> rs = client.sql().execute(null,
                "SELECT customer_id, data_usage, sms_usage, call_usage "
                        + "FROM Subscriber ORDER BY customer_id")) {
            while (rs.hasNext()) {
                SqlRow row = rs.next();
                subscribers.add(new Subscriber(
                        row.intValue("CUSTOMER_ID"),
                        row.doubleValue("DATA_USAGE"),
                        row.intValue("SMS_USAGE"),
                        row.intValue("CALL_USAGE")));
            }
        }
        return subscribers;
    }
}
