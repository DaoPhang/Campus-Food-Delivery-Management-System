/**
 * CustomHashMap implements a Hash Table data structure from scratch.
 * Strategy: Separate Chaining with Linked Lists.
 * Time Complexity: Average O(1) for put/get.
 */
public class CustomHashMap<K, V> {
    
    // Internal node class for the Linked List in each bucket
    private class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    // The array of buckets (Linked Lists)
    private Entry<K, V>[] buckets;
    private int capacity;  // Size of the array
    private int size;      // Total number of key-value pairs

    // Default capacity (Prime number preferred for better distribution)
    private static final int DEFAULT_CAPACITY = 101; 

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.capacity = DEFAULT_CAPACITY;
        // Create array of Entry objects
        this.buckets = new Entry[capacity];
        this.size = 0;
    }

    /**
     * Hash function to map key to an index.
     * Uses Java's object hashCode() and modulo operator.
     */
    private int getBucketIndex(K key) {
        int hashCode = key.hashCode();
        // Use Math.abs to ensure positive index
        return Math.abs(hashCode) % capacity;
    }

    /**
     * Put a key-value pair into the map.
     * If key exists, update value. If not, add new entry.
     */
    public void put(K key, V value) {
        int index = getBucketIndex(key);
        Entry<K, V> head = buckets[index];

        // Check if key already exists in the chain
        Entry<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // Update existing value
                return;
            }
            current = current.next;
        }

        // Key not found, insert new node at the HEAD of the chain (faster)
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;
    }

    /**
     * Get value by key.
     * Returns null if key is not found.
     */
    public V get(K key) {
        int index = getBucketIndex(key);
        Entry<K, V> current = buckets[index];

        // Traverse the linked list at this bucket
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        return null; // Not found
    }

    /**
     * Check if map contains a key.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
}
