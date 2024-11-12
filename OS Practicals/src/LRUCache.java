import java.util.HashMap;
import java.util.LinkedHashSet;

class LRUCache {
    private int capacity;
    private LinkedHashSet<Integer> recentlyUsed;
    private HashMap<Integer, Integer> pageToCacheIndex;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.recentlyUsed = new LinkedHashSet<>();
        this.pageToCacheIndex = new HashMap<>();
    }

    public int get(int page) {
        if (!pageToCacheIndex.containsKey(page)) {
            return -1; // Page not found in the cache
        }

        // Update the recently used order
        recentlyUsed.remove(page);
        recentlyUsed.add(page);

        return pageToCacheIndex.get(page);
    }

    public void put(int page, int value) {
        if (pageToCacheIndex.containsKey(page)) {
            // Update the recently used order
            recentlyUsed.remove(page);
            recentlyUsed.add(page);
        } else {
            if (recentlyUsed.size() == capacity) {
                // Cache is full, evict the least recently used page
                int leastRecentlyUsedPage = recentlyUsed.iterator().next();
                recentlyUsed.remove(leastRecentlyUsedPage);
                pageToCacheIndex.remove(leastRecentlyUsedPage);
            }
            // Add the new page to the cache
            recentlyUsed.add(page);
        }

        pageToCacheIndex.put(page, value);
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(3); // Cache capacity is 3

        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        System.out.println(cache.get(1)); // Output: 1
        cache.put(4, 4);
        System.out.println(cache.get(2)); // Output: -1 (2 is evicted)
        System.out.println(cache.get(3)); // Output: 3
        System.out.println(cache.get(4)); // Output: 4
    }
}
