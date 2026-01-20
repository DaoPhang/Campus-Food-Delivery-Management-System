# Campus Food Delivery Management System

WIA1002 Data Structures - Degree Level Project

## 📁 Project Structure

```
Campus-Food-Delivery-Management-System/
├── src/                    # Java source files
│   ├── Main.java           # Main entry point with menu system
│   ├── Graph.java          # Graph implementation with Dijkstra
│   ├── CampusMap.java      # Campus map wrapper
│   ├── DispatchSystem.java # Rider dispatch with weighted optimization
│   ├── OrderSystem.java    # Order management with priority queue
│   ├── Rider.java          # Rider class with job tracking
│   ├── Order.java          # Order class
│   ├── DispatchAction.java # Undo/redo state storage
│   ├── PathResult.java     # Path caching result
│   ├── SystemStatistics.java # Performance statistics
│   ├── Edge.java           # Graph edge
│   ├── Location.java       # Location node
│   ├── NodeDistanceCompare.java # Dijkstra helper
│   └── Test.java           # Test file
│
├── data/                   # Data files for persistence
│   ├── locations.txt       # Campus locations
│   ├── routes.txt          # Routes with distances
│   ├── riders.txt          # Rider data
│   └── orders.txt          # Order data
│
├── docs/                   # Documentation
│   ├── WIA1002 Campus Food Delivery Management System.pdf
│   └── MEMBER3_COORDINATION_NOTES.md
│
└── README.md               # This file
```

## 🚀 How to Compile and Run

### Option 1: From Project Root
```bash
cd Campus-Food-Delivery-Management-System
javac -d out src/*.java
java -cp out Main
```

### Option 2: From src folder
```bash
cd Campus-Food-Delivery-Management-System/src
javac *.java
cd ..
java -cp src Main
```

## ✨ Features (5 Upgrades Implemented)

### 1️⃣ Smart Dispatch v2 (Weighted Optimization)
- Uses weighted scoring: `score = (1.0×distance) + (2.0×jobs) - (0.5×idleTime/1000)`
- Balances efficiency and fairness in rider selection

### 2️⃣ File Persistence (Save & Load)
- Load/save locations, routes, riders, and orders from text files
- System state survives program restart

### 3️⃣ Undo/Redo System (Stack-based)
- Two stacks for undo and redo operations
- Preserves correct action history

### 4️⃣ Path Caching (HashMap Optimization)
- Caches Dijkstra results to avoid repeated calculations
- Tracks cache hit rate and statistics

### 5️⃣ System Performance Statistics
- Tracks total orders, distances, Dijkstra calls
- Displays comprehensive performance metrics

## 📊 Data Structures Used

| Data Structure | Purpose |
|----------------|---------|
| HashMap | O(1) lookup for riders, orders, path cache |
| PriorityQueue | Order priority processing |
| Stack (x2) | Undo and redo operations |
| ArrayList | Rider and order storage |
| Adjacency List | Graph representation |

## 👥 Team Members

- Member 1: Graph & Campus Map
- Member 2: Orders & Priority System
- Member 3: Dispatch System & Undo/Redo
- Member 4: Main Menu & Integration
