# Member 3 - Coordination Notes & Requirements

## ✅ What You've Completed

1. ✅ Rider class with all required fields
2. ✅ RiderStatus enum (Available, Delivering, Offline)
3. ✅ ArrayList and HashMap for storing riders
4. ✅ searchRiderByID() method (via getRider())
5. ✅ listAvailableRiders() method
6. ✅ assignOrder() method with path calculation
7. ✅ completeOrder() method with proper status updates

## 🔗 CRITICAL: Coordinate with Other Members

### With Member 1 (Graph & Campus Map):

**REQUIRED METHODS:**
Your `assignOrder()` method needs these methods from `CampusMap`:

1. **`getDistance(String from, String to)`** - Returns shortest distance (double)
   - Should return -1 or throw exception if locations don't exist or no path
   - Currently assumed to exist ✓

2. **`getShortestPath(String from, String to)`** - Returns `List<String>` of locations
   - **THIS IS OPTIONAL BUT RECOMMENDED** for better path display
   - If not available, your code will fallback to showing just locations
   - **ASK MEMBER 1:** Can they add this method? It should return the sequence of locations

**EXAMPLE:**
```java
// If Member 1 implements getShortestPath:
List<String> path = map.getShortestPath("Kolej A", "Cafeteria B");
// Returns: ["Kolej A", "Main Road", "Cafeteria B"]
```

### With Member 2 (Orders & Priority System):

**REQUIRED METHODS:**
Your code uses these methods from `OrderSystem`:

1. ✅ `getNextPendingOrder()` - Returns Order (or null)
2. ✅ `pollPriorityQueue()` - Removes order from priority queue
3. ✅ `searchOrder(String orderID)` - Returns Order (or null)

**REQUIRED FROM Order CLASS:**
- `getId()` - String
- `getStudentName()` - String
- `getPickupLocation()` - String
- `getDeliveryLocation()` - String
- `getStatus()` - String
- `setStatus(String status)` - void

**VERIFY:** Make sure Order status values match:
- "Pending" (initial)
- "Delivering" (when assigned)
- "Delivered" (when completed)
- "Cancelled" (if cancelled)

### With Member 4 (Main Menu):

**METHODS TO EXPOSE:**
1. ✅ `displayAllRiders()` - For menu option "View riders"
2. ✅ `displayAvailableRiders()` - Shows only available riders
3. ✅ `assignOrder(OrderSystem, CampusMap)` - For menu option "Assign order"
4. ✅ `completeOrder(String orderID, OrderSystem)` - For menu option "Complete order"
5. ✅ `getRider(String id)` - For menu option "Search rider"
6. ✅ `addRider(Rider)` - For data setup/preloading

## 📋 Testing Checklist

Before integration, test these scenarios:

- [ ] Assign order when no pending orders exist
- [ ] Assign order when no riders available
- [ ] Assign order with invalid locations (should handle gracefully)
- [ ] Complete order that doesn't exist
- [ ] Complete order that's not in "Delivering" status
- [ ] Complete order successfully (verify rider location updates)
- [ ] Search rider by ID (existing and non-existing)
- [ ] Display all riders (empty list, with different statuses)

## 🎯 Improvements Made

1. ✅ **Better error handling** - Validates paths, handles exceptions
2. ✅ **Path display** - Shows full route sequence (if Member 1 provides path method)
3. ✅ **Better output formatting** - Clear, professional display messages
4. ✅ **Status validation** - Checks order status before completion
5. ✅ **Additional utility methods** - displayAllRiders(), getAvailableRiderCount()

## ⚠️ Important Notes

1. **Path Display:** Currently shows locations as fallback. If Member 1 adds `getShortestPath()`, uncomment lines 91-94 in `assignOrder()` for full path display.

2. **Status Values:** Make sure Order status strings match exactly:
   - Use "Delivering" (not "Assigned") when order is assigned
   - Coordinate with Member 2 on exact status values

3. **Location Names:** Rider locations must match location names in CampusMap exactly (case-sensitive). Coordinate with Member 1 on location naming conventions.

4. **Order Removal:** Currently removes order from priority queue AFTER validation. This is correct - don't remove if validation fails.

## 📝 For Your Report & Video

**Explain:**
1. How riders are stored (ArrayList + HashMap for O(1) lookup)
2. How order-rider linking works (currentOrderId field)
3. How dispatch logic integrates with graph (shortest path calculation)
4. How rider status transitions work (Available → Delivering → Available)
5. Time complexity of search operations (O(1) for HashMap lookup)

