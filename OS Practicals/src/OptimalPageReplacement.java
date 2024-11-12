import java.util.*;

public class OptimalPageReplacement {
    public static int findOptimalPage(int[] pages, int capacity) {
        // Create a map to store the future reference of each page
        Map<Integer, Integer> pageMap = new HashMap<>();

        // Iterate through the pages
        for (int i = 0; i < pages.length; i++) {
            // If the page is not present in the cache
            if (!pageMap.containsKey(pages[i])) {
                // If the cache is full, find the page that will not be used for the longest time in the future
                if (pageMap.size() == capacity) {
                    int maxIndex = -1;
                    int pageToReplace = -1;
                    for (Map.Entry<Integer, Integer> entry : pageMap.entrySet()) {
                        if (entry.getValue() > maxIndex) {
                            maxIndex = entry.getValue();
                            pageToReplace = entry.getKey();
                        }
                    }
                    pageMap.remove(pageToReplace);
                }
                // Add the new page to the cache
                pageMap.put(pages[i], i + 1);
            } else {
                // Update the future reference of the existing page
                pageMap.put(pages[i], i + 1);
            }
        }

        // Return the number of page faults
        return pageMap.size();
    }

    public static void main(String[] args) {
        int[] pages = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2};
        int capacity = 4;
        int pageFaults = findOptimalPage(pages, capacity);
        System.out.println("Number of page faults: " + pageFaults);
    }
}