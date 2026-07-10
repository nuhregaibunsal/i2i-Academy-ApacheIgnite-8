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

The program connects to the node, (re)creates and clears the `Subscriber` table,
inserts five baseline subscribers, simulates random usage, and prints the final state.

---

## Solution

### 3.1.1. Theoretical Knowledge

**What is Apache Ignite, and how does its In-Memory Data Grid (IMDG) architecture differ
from a pure key-value store like Redis?**

Apache Ignite is a distributed database and in-memory computing platform that keeps data in
RAM across a cluster while optionally persisting it to disk. Unlike a pure key-value store
such as Redis, its IMDG partitions data across nodes with built-in redundancy and supports
distributed SQL, ACID transactions, and co-located compute that runs *next to* the data.
So where Redis is primarily a fast cache/key-value engine, Ignite behaves like a horizontally
scalable, SQL-capable distributed database that also happens to live in memory.

**In the context of Apache Ignite, what is the architectural difference between a Thick Client
and a Thin Client?**

A Thick (server) client joins the cluster topology as a full member — it holds cluster
metadata and can host compute — which is powerful but heavier and tightly version-coupled.
A Thin Client is a lightweight connector that talks to the cluster over a single TCP socket
(port `10800`) using a binary protocol, without joining the topology, which makes it simpler,
lighter, and the recommended choice for most applications — as used in this project.

### 3.1.2 / 3.1.3 Implementation Notes

| Requirement | Where it is handled |
|-------------|---------------------|
| Ignite 3 container + exposed `10800` | [`docker-compose.yml`](docker-compose.yml) |
| Thin Client (`IgniteClient.builder()`) | [`IgniteConnection.java`](src/main/java/com/i2i/academy/ignite/IgniteConnection.java) |
| `Subscriber` model (customerId PK, dataUsage, smsUsage, callUsage) | [`Subscriber.java`](src/main/java/com/i2i/academy/ignite/Subscriber.java) |
| Create `Subscriber` table | `SubscriberRepository.createTableIfAbsent()` |
| Clear table on every run | `SubscriberRepository.clear()` (`DELETE FROM Subscriber`) |
| Insert 5 baseline subscribers | `App.insertBaselineSubscribers()` |
| Simulate usage + UPDATE records | `App.simulateUsage()` |
| Print final state and terminate | `App.printFinalState()` |

### Sample Console Output

```
Connected to Ignite node(s): [ClientConnection [nodeName=node1, addr=127.0.0.1/127.0.0.1:10800]]
Subscriber table is ready and cleared.
Inserted 5 baseline subscribers.
Simulated random usage and updated all subscribers.

----- Final Subscriber State -----
Subscriber{customerId=1, dataUsage=612.47 MB, smsUsage=38, callUsage=214 min}
Subscriber{customerId=2, dataUsage=173.05 MB, smsUsage=91, callUsage=57 min}
Subscriber{customerId=3, dataUsage=944.62 MB, smsUsage=12, callUsage=176 min}
Subscriber{customerId=4, dataUsage=428.19 MB, smsUsage=64, callUsage=289 min}
Subscriber{customerId=5, dataUsage=87.33 MB, smsUsage=45, callUsage=133 min}
----------------------------------
```

> Usage values are randomized each run, so exact numbers will differ.

### Shutting Down

```bash
docker compose down          # stop the node
docker compose down -v       # stop and remove data volumes
```
