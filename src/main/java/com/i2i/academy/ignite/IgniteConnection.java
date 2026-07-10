package com.i2i.academy.ignite;

import org.apache.ignite.client.IgniteClient;

/**
 * Small factory that opens a connection to the local Apache Ignite 3 node using
 * the modern Thin Client API.
 *
 * <p>The target address can be overridden with the {@code IGNITE_ADDRESS}
 * environment variable (host:port), which is handy when the node runs somewhere
 * other than the default {@code 127.0.0.1:10800}.
 */
public final class IgniteConnection {

    private static final String DEFAULT_ADDRESS = "127.0.0.1:10800";

    private IgniteConnection() {
        // Utility class - no instances.
    }

    /**
     * Opens a new thin-client connection. The returned client is
     * {@link AutoCloseable}, so callers should use it inside a try-with-resources
     * block.
     */
    public static IgniteClient open() {
        String address = System.getenv().getOrDefault("IGNITE_ADDRESS", DEFAULT_ADDRESS);
        return IgniteClient.builder()
                .addresses(address)
                .build();
    }
}
