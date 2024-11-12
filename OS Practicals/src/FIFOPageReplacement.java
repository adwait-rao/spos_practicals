import java.util.LinkedList;
import java.util.Queue;

public class FIFOPageReplacement {
    public static int findFIFOPageFaults(int[] pages, int capacity) {
        // Create a queue to store the pages
        Queue<Integer> pageQueue = new LinkedList<>();
        int pageFaults = 0;

        // Iterate through the pages
        for (int page : pages) {
            // If the page is not present in the cache
            if (!pageQueue.contains(page)) {
                // If the cache is full, remove the oldest page
                if (pageQueue.size() == capacity) {
                    pageQueue.poll();
                }
                // Add the new page to the cache
                pageQueue.offer(page);
                pageFaults++;
            }
        }

        return pageFaults;
    }

    public static void main(String[] args) {
        int[] pages = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2};
        int capacity = 4;
        int pageFaults = findFIFOPageFaults(pages, capacity);
        System.out.println("Number of page faults: " + pageFaults);
    }
}