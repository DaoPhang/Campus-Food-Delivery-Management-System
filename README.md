# Campus Food Delivery Management System

## 📖 Project Overview
The **Campus Food Delivery Management System** is a Java-based console application designed to simulate and manage the logistics of food delivery within a university campus. The system handles order placement, rider dispatching, route optimization, and system monitoring.

**Key Technical Constraint:** This project is built using **custom manual data structures** (Arrays, Linked Lists logic via arrays, Stacks, Queues) without relying on Java's built-in Collections Framework (e.g., `ArrayList`, `HashMap`, `LinkedList`, `PriorityQueue` are **NOT** used).

---

## 📂 Project Structure

```text
src/
├── Main.java                // Entry point and Menu System
├── CampusMap.java           // High-level interface for the Map
├── Graph.java               // Core graph algorithms (Dijkstra, Adjacency List)
├── DispatchSystem.java      // Rider management and dispatch logic
├── OrderSystem.java         // Order management and Queue system
├── Rider.java               // Rider entity
├── Order.java               // Order entity
├── Location.java            // Location entity
├── Edge.java                // Graph edge entity
├── DispatchAction.java      // State object for Undo/Redo history
├── PathResult.java          // Helper class for Path Caching
└── SystemStatistics.java    // Singleton class for global metrics
data/
├── locations.txt            // Map nodes
├── routes.txt               // Map edges (distances)
├── riders.txt               // Rider data
└── orders.txt               // Order data
```

---

## 🏗 Class Descriptions (Detailed)

### 1. Main.java
*   **Role**: The central controller of the application.
*   **Functionality**:
    *   Initializes all subsystems (`Graph`, `DispatchSystem`, `OrderSystem`).
    *   Provides a Command Line Interface (CLI) with a main menu.
    *   Handles user inputs and routes them to the appropriate system methods.
    *   Demonstrates the integration of all 5 system upgrades.

### 2. Graph.java
*   **Role**: Represents the campus map using a graph data structure.
*   **Functionality**:
    *   **Manual Adjacency List**: Uses `Edge[][]` and `int[]` arrays to store connections between locations.
    *   **Dijkstra's Algorithm**: Implements the shortest path algorithm using manual priority queue logic (arrays).
    *   **Path Caching**: Stores frequently calculated paths in `PathResult[]` arrays to optimize performance (Upgrade 4).
    *   **File I/O**: Loads locations and routes from text files.

### 3. DispatchSystem.java
*   **Role**: Manages the fleet of riders and handles the assignment logic.
*   **Functionality**:
    *   **Rider Management**: Stores riders in a fixed-size array `Rider[]`.
    *   **Weighted Dispatch Algorithm (Upgrade 1)**: Calculates a score for each rider based on distance, workload, and idle time to find the optimal assignment.
    *   **Undo/Redo System (Upgrade 3)**: Uses two manual stacks (`DispatchAction[]`) to track and reverse dispatch decisions.
    *   **Statistics**: Tracks total orders dispatched and distance covered.

### 4. OrderSystem.java
*   **Role**: Manages customer orders.
*   **Functionality**:
    *   **Order Queue**: Implements a manual FIFO (First-In-First-Out) queue using an `Order[]` array with `front` and `rear` pointers.
    *   **Order Lookup**: Provides linear search capabilities to find orders by ID or Student Name.
    *   **Priority Handling**: Inserts orders into the queue based on priority level.

### 5. CampusMap.java
*   **Role**: A wrapper class acting as a facade for `Graph.java`.
*   **Functionality**: Simplifies method calls for the `Main` class (e.g., `getDistance`, `getShortestPath`) and delegates complex logic to the `Graph`.

### 6. SystemStatistics.java (Upgrade 5)
*   **Role**: A Singleton class for tracking global system performance.
*   **Functionality**: Aggregates data from all systems to display a comprehensive report (Total Orders, Average Delivery Time, Cache Hit Rates, etc.).

### 7. Entity Classes
*   **Rider.java**: Stores rider details (ID, Name, Location, Status, Jobs Completed).
*   **Order.java**: Stores order details (ID, Student Name, Pickup/Delivery Locations, Status).
*   **Location.java**: Represents a node on the map (Name, Faculty, Block).
*   **Edge.java**: Represents a connection between two locations with a weight (distance).
*   **DispatchAction.java**: A "snapshot" object storing the details of a dispatch event, used specifically for the Undo/Redo stacks.
*   **PathResult.java**: A helper object to store the result of a pathfinding operation (nodes and total distance) for the cache.

---

## 🚀 System Features & Upgrades

This project implements 5 specific upgrades to the base system:

1.  **Weighted Optimization Dispatch**:
    *   Instead of random assignment, riders are chosen based on a weighted score:
    *   `Score = (1.0 * Distance) + (2.0 * Jobs_Completed) - (0.5 * Idle_Time)`
    *   Ensures efficiency and fairness.

2.  **File Persistence**:
    *   Ability to **Save** current state (Riders, Orders) to `.txt` files.
    *   Ability to **Load** system state from `.txt` files on startup.

3.  **Undo/Redo Capability**:
    *   **Undo**: Reverses the last dispatch action, returning the order to "Pending" and the rider to "Available".
    *   **Redo**: Re-applies the dispatch action that was just undone.
    *   Implemented using manual Stack data structures.

4.  **Path Caching**:
    *   Stores the result of Dijkstra's algorithm.
    *   If a route is requested again, it is retrieved from the cache (O(1)) instead of recalculating (O(V+E)), significantly improving performance.

5.  **Performance Statistics**:
    *   Real-time tracking of metrics like "Average Distance per Order", "Cache Hit Rate", and "Rider Utilization".

---

## 📖 User Guide

### 1. Order Management
*   **Add Order**: Enter details (ID, Name, Locations). Order goes to the Pending Queue.
*   **View Orders**: See lists of Pending, Delivering, or Completed orders.
*   **Search**: Find orders by ID or Student Name.

### 2. Rider Management
*   **Add Rider**: Register a new rider with a starting location.
*   **View Riders**: See status (Available/Delivering) and location of all riders.

### 3. Dispatching
*   **Assign Next Order**: The system automatically picks the highest priority order and assigns it to the best available rider using the Weighted Algorithm.
*   **Complete Order**: Mark an order as "Delivered". The rider becomes available at the delivery location.
*   **Undo/Redo**: Mistake in dispatching? Use Undo to revert.

### 4. Map Operations
*   **View Map**: See all connections.
*   **Shortest Path**: Enter two locations to see the optimal route and distance.

---

## 👥 Team Roles

| Role | Responsibilities | Member Name |
| :--- | :--- | :--- |
| **Team Lead** | Project Architecture, Main Menu, Integration | [Name] |
| **Algorithm Specialist** | Graph Implementation, Dijkstra's Algo, Path Caching | [Name] |
| **Logic Specialist** | Dispatch Algorithm, Undo/Redo Stacks | [Name] |
| **Data Specialist** | Order Queue, File I/O, Data Models | [Name] |
| **QA Specialist** | Testing, Debugging, Statistics Module | [Name] |

---

## ⚙️ Installation

1.  **Compile**:
    ```bash
    javac src/*.java
    ```
2.  **Run**:
    ```bash
    java -cp src Main
    ```
