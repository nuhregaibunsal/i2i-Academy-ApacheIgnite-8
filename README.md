# i2i-Academy-ApacheIgnite-8

A hands-on project that explores **In-Memory Data Grids (IMDG)** and **distributed databases**
by running a single-node **Apache Ignite 3** cluster in Docker and driving it with a modern
**Java Thin Client**.

The client models mobile-network **subscribers** and their usage (data, SMS, calls), then
simulates usage updates against the in-memory grid.

## Tech Stack

| Component        | Choice                          |
|------------------|---------------------------------|
| Data grid        | Apache Ignite 3.0.0             |
| Runtime          | Docker / Docker Compose         |
| Client language  | Java 11                         |
| Build tool       | Maven                           |
| Client API       | Ignite 3 Thin Client (`IgniteClient.builder()`) |

## Project Layout

```
.
├── docker-compose.yml          # Single-node Ignite 3 cluster
├── config/
│   └── ignite-config.conf      # Node network configuration
├── pom.xml                     # Maven build + Ignite client dependency
└── src/main/java/com/i2i/academy/ignite/
    ├── Subscriber.java             # Data model
    ├── IgniteConnection.java       # Thin-client connection factory
    ├── SubscriberRepository.java   # Table lifecycle + CRUD via SQL
    └── App.java                    # Program entry point / simulation
```

## 1. Start the Ignite Cluster

```bash
docker compose up -d
```

This starts one Ignite 3 node named `node1`, exposing:

- **10800** — thin client port (used by the Java app)
- **10300** — REST API (used to initialize the cluster)

## 2. Initialize the Cluster (one-time)

A freshly started Ignite 3 node must be activated before it accepts data operations:

```bash
docker run --rm -it --network=host apacheignite/ignite:3.0.0 cli
# inside the CLI:
connect http://localhost:10300
cluster init --name=ignite3
cluster status      # should report ACTIVE
```

## 3. Run the Java Client

```bash
mvn compile exec:java
```

> Detailed run output and the theoretical background are documented in the
> [Solution](#solution) section below.

## Solution

_This section is completed as the implementation progresses._
