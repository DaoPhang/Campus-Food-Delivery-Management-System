# Campus Food Delivery Management System

## 👥 Team Members
1. **Tan Dao Phang** (24076042)
2. **An Jun Li** (23122616)
3. **Yang Pu** (24088528)
4. **Teow Yan Ping** (24063218)

## 📺 Project Demo
[Click here to watch our 5-minute Project Walkthrough](#) *(Insert YouTube/Drive Link Here)*

## 📖 Project Overview
The **Campus Food Delivery Management System** is a hybrid (Console + Web) application designed to simulate and manage the logistics of food delivery within a university campus. The system handles order placement, rider dispatching, route optimization, and real-time monitoring.

**Key Technical Constraint:** This project is built using **custom manual data structures** without relying on Java's built-in Collections Framework.
* ❌ No `java.util.HashMap` → ✅ **CustomHashMap (Separate Chaining)**
* ❌ No `java.util.PriorityQueue` → ✅ **Manual Sorted Array Logic**
* ❌ No `java.util.ArrayList` → ✅ **Dynamic Resizing Arrays**

---

## 📂 Project Structure

```text
src/
├── Main.java                // Entry point (Launches Console + Web Server)
├── WebServer.java           // Custom HTTP Server for Dashboard (No frameworks)
├── ConsoleUI.java           // Utility class for rich text formatting & menus
├── CustomHashMap.java       // Manual Hash Table implementation (O(1) lookup)
├── CampusMap.java           // High-level interface for the Map
├── Graph.java               // Core graph algorithms (Dijkstra, Adjacency List)
├── DispatchSystem.java      // Rider management and dispatch logic
├── OrderSystem.java         // Order management and Priority Queue
├── Rider.java               // Rider entity
├── Order.java               // Order entity
├── Location.java            // Location entity
├── Edge.java                // Graph edge entity
├── DispatchAction.java      // State object for Undo/Redo history
├── PathResult.java          // Helper class for Path Caching
└── SystemStatistics.java    // Singleton class for global metrics
web/
├── dashboard.html           // Real-time Cyberpunk Graph Visualizer
└── admin.html               // Web-based Admin Control Panel
data/
├── locations.txt            // Map nodes
├── routes.txt               // Map edges (distances)
├── riders.txt               // Rider data
└── orders.txt               // Order data
```

---

## 🚀 System Features & Upgrades

This project implements **6 specific upgrades** to the base requirements:

1.  **Custom Data Structures (Mastery)**:
    * Implemented `CustomHashMap<K,V>` from scratch using bucket arrays and linked-list collision handling for O(1) rider/order lookups.

2.  **Hybrid Interface (Console + Web)**:
    * **Console**: Robust CLI for standard operations.
    * **Web Dashboard**: A real-time HTML5 Canvas visualization of the graph, showing riders moving between nodes and live order statuses. Built using a custom threaded `HttpServer`.

3.  **Weighted Optimization Dispatch**:
    * Riders are chosen based on a weighted score:
    * `Score = (1.0 * Distance) + (2.0 * Jobs_Completed) - (0.5 * Idle_Time)`

4.  **File Persistence**:
    * Ability to **Save/Load** system state (Riders, Orders, Locations) to `.txt` files.

5.  **Undo/Redo Capability**:
    * Implemented using manual Stack data structures to reverse dispatch actions.

6.  **Path Caching**:
    * Stores the result of Dijkstra's algorithm. If a route is requested again, it is retrieved from the cache (O(1)) instead of recalculating (O(V+E)).

---

## ✅ Requirements Compliance Checklist

| Requirement | Implementation Detail | Location |
| :--- | :--- | :--- |
| **Graph (Adjacency List)** | Custom `Edge[][]` array structure | `Graph.java` |
| **Priority Queue** | Manual sorted insertion logic (Insertion Sort) | `OrderSystem.java` |
| **Lookup (Dictionary)** | **Custom Hash Map** (Separate Chaining) | `CustomHashMap.java` |
| **Shortest Path** | Dijkstra's Algorithm (Manual Implementation) | `Graph.java` |
| **Stack/Queue** | Manual Array-based Stack (Undo/Redo) and Queue | `DispatchSystem.java` |

---

## 📖 User Guide

### 1. Launching the System
1.  Run the application via `Main.java`.
2.  The **Console Menu** will appear for standard inputs.
3.  The **Web Server** automatically starts at `http://localhost:8080`.
    * **Visualization:** Open `http://localhost:8080/dashboard.html` to see the live graph.
    * **Admin Panel:** Open `http://localhost:8080/admin.html` to add orders/riders via Web UI.

### 2. Operations
* **Assign Next Order**: The system automatically picks the highest priority order and assigns it to the best available rider using the Weighted Algorithm.
* **View Map**: Use option 4 in Console or view the Web Dashboard.
* **Undo/Redo**: Mistake in dispatching? Use Undo to revert.

---

## 👥 Team Roles

| Role | Responsibilities | Member Name |
| :--- | :--- | :--- |
| **Member 1 (Graph & Algorithm)** | Graph Data Structure, Dijkstra's Algorithm, Path Caching, Map Management | AN JUN LI |
| **Member 2 (Orders & Priority)** | Order Queue Implementation, Priority Logic, Order Data Management | TEOW YAN PING |
| **Member 3 (Dispatch & Integration)** | Dispatch Algorithm, Rider Management, Undo/Redo System, System Integration | TAN DAO PHANG |
| **Member 4 (UI & Presentation)** | Main Menu, Web Server Integration, Documentation, Final Presentation | YANG PU |

---

## ⚙️ Installation

### Prerequisites
* **Java Development Kit (JDK)**: Version 8 or higher.

1.  **Compile**:
    ```bash
    javac src/*.java
    ```
2.  **Run**:
    ```bash
    java -cp src Main
    ```
3.  **Access Web UI**:
    Open `http://localhost:8080/dashboard.html` in your browser.
