# Campus Food Delivery Management System

WIA1002 Data Structures

## 📖 Project Overview

The **Campus Food Delivery Management System** is a Java-based console application designed to simulate and manage food deliveries within a university campus. It utilizes advanced data structures and algorithms to optimize rider dispatching, calculate shortest paths, and manage orders efficiently.

This project demonstrates the practical application of:
- **Graphs & Dijkstra's Algorithm** for route optimization.
- **Priority Queues** for order management.
- **HashMaps** for O(1) lookups and caching.
- **Stacks** for undo/redo functionality.

---

## ✨ Key Features (5 Major Upgrades)

This system includes 5 specific upgrades that enhance functionality and performance:

### 1️⃣ Smart Dispatch System (Weighted Optimization)
Instead of simply choosing the nearest rider, the system uses a **weighted scoring algorithm** to select the best rider for each job.
- **Formula**: `Score = (1.0 × Distance) + (2.0 × JobsCompleted) - (0.5 × IdleTime)`
- **Goal**: Balances efficiency (distance) with fairness (workload distribution) and responsiveness (idle time).
- **Benefit**: Prevents one rider from doing all the work while others sit idle.

### 2️⃣ File Persistence (Save & Load)
The system state is no longer lost when the program closes.
- **Data Files**: Locations, routes, riders, and orders are stored in `data/*.txt` files.
- **Functionality**: You can save current progress and reload it upon restarting the application.

### 3️⃣ Undo/Redo System
Mistakes happen! The dispatch system includes a robust undo/redo mechanism.
- **Data Structure**: Uses two `Stack` data structures (`undoStack` and `redoStack`).
- **Capability**: You can revert accidental dispatch assignments and re-apply them if needed.

### 4️⃣ Path Caching (Optimization)
To improve performance, the system caches the results of expensive pathfinding calculations.
- **Mechanism**: Stores calculated shortest paths in a `HashMap`.
- **Benefit**: If a route is requested again, it's retrieved instantly (O(1)) instead of re-running Dijkstra's algorithm.
- **Stats**: Tracks cache hits, misses, and hit rates.

### 5️⃣ System Performance Statistics
A comprehensive dashboard to monitor system health and efficiency.
- **Metrics**: Tracks total orders, total distance, average distance per order, algorithm performance (Dijkstra calls), and system uptime.

---

## 📂 Class Descriptions

Here is a detailed breakdown of the Java classes used in this project:

### Core Logic
- **`Main.java`**: The entry point of the application. It handles the command-line interface (CLI), menu navigation, and system initialization.
- **`CampusMap.java`**: A high-level wrapper for the graph. It provides easy-to-use methods for adding locations/routes and finding paths, abstracting the complex graph logic.
- **`Graph.java`**: The heart of the mapping system. Implements the **Adjacency List** graph structure and **Dijkstra's Algorithm** for shortest path finding. It also handles the **Path Caching** logic.
- **`DispatchSystem.java`**: Manages the fleet of riders. It contains the **Weighted Dispatch Algorithm**, handles the **Undo/Redo Stacks**, and tracks rider statistics.
- **`OrderSystem.java`**: Manages customer orders. Uses a **PriorityQueue** to ensure high-priority orders are processed first and a **HashMap** for fast order lookups.

### Data Models
- **`Rider.java`**: Represents a delivery rider. Tracks their current location, status (Available/Delivering), job history, and performance stats.
- **`Order.java`**: Represents a food order. Stores details like student name, pickup/delivery locations, priority, and status. Implements `Comparable` for priority sorting.
- **`Location.java`**: Represents a node in the graph (e.g., a specific building or block).
- **`Edge.java`**: Represents a connection (road/path) between two locations with a specific distance (weight).
- **`PathResult.java`**: A helper class used to store cached path data (distance + list of steps).
- **`DispatchAction.java`**: A state object that records the details of a dispatch event (Order ID, Rider ID, previous location) to enable undo/redo.

### Utilities
- **`SystemStatistics.java`**: A **Singleton** class that aggregates performance metrics from across the entire system for the statistics dashboard.
- **`NodeDistance.java`**: A helper class used inside Dijkstra's algorithm to pair a node with its current shortest distance in the priority queue.

---

## 🚀 How It Works (User Guide)

### 1. Initialization
When you start the app, it automatically loads data from the `data/` folder.
- **Locations & Routes**: Builds the campus graph.
- **Riders & Orders**: Restores the previous state of the fleet and queue.

### 2. Placing an Order (`Order Management`)
- Go to **Order Management** -> **Add New Order**.
- Enter details: Student Name, Pickup, Delivery, and Priority (1 = Highest).
- The order enters the **Priority Queue**. High-priority orders jump to the front!

### 3. Dispatching a Rider (`Dispatch Operations`)
- Go to **Dispatch Operations** -> **Assign Next Order**.
- The system looks at the top order in the queue.
- It calculates a **Score** for every available rider based on:
    1.  How close they are to the pickup.
    2.  How many jobs they've already done (to balance load).
    3.  How long they've been idle.
- The rider with the **lowest score** is assigned.
- The route is displayed, and the rider moves to "Delivering" status.

### 4. Completing an Order
- Once the rider arrives, go to **Dispatch Operations** -> **Complete Order**.
- Enter the Order ID.
- The rider becomes "Available" at the delivery location, ready for the next job.
- Stats (distance, jobs completed) are updated.

### 5. Using Undo/Redo
- Made a mistake assigning an order? Select **Undo Last Dispatch**.
- The order returns to "Pending", and the rider returns to their previous location.
- Changed your mind? Select **Redo Last Undo** to re-apply the assignment.

### 6. Viewing the Map & Stats
- **Campus Map**: View the graph connections or find the shortest path between two specific points.
- **System Statistics**: Check the "System Statistics" menu to see how well the algorithm is performing (Cache Hit Rate, Average Distance, etc.).

---

## 🛠️ Installation & Execution

### Prerequisites
- Java Development Kit (JDK) 8 or higher.

### Option 1: Run from Project Root (Recommended)
```bash
cd Campus-Food-Delivery-Management-System
javac -d out src/*.java
java -cp out Main
```

### Option 2: Run from Source Directory
```bash
cd Campus-Food-Delivery-Management-System/src
javac *.java
cd ..
java -cp src Main
```

---

## 📁 Project Structure

```
Campus-Food-Delivery-Management-System/
├── src/                    # Source Code
│   ├── Main.java           # Entry Point
│   ├── Graph.java          # Graph & Dijkstra
│   ├── DispatchSystem.java # Rider Management
│   ├── OrderSystem.java    # Order Management
│   └── ... (other classes)
│
├── data/                   # Persistent Data
│   ├── locations.txt
│   ├── routes.txt
│   ├── riders.txt
│   └── orders.txt
│
├── docs/                   # Documentation
│   └── ...
│
└── README.md               # This file
```

---

## 👥 Team Members

- Member 1: Graph & Campus Map
- Member 2: Orders & Priority System
- Member 3: Dispatch System & Undo/Redo
- Member 4: Main Menu & Integration
