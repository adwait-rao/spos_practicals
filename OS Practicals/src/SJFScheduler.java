class Process2 {
    int pid;
    int arrivalTime;
    int burstTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;
    boolean isCompleted;

    public Process2(int pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.isCompleted = false;
    }
}
/**
 This implementation of SJF is non-preemptive
*/

public class SJFScheduler {
    private Process2[] processes;
    private int n;
    private int currentTime;

    public SJFScheduler(Process2[] processes) {
        this.processes = processes;
        this.n = processes.length;
        this.currentTime = 0;
    }

    public void schedule() {
        int completed = 0;

        // continue until all processes are completed
        while (completed != n) {
            int shortestJob = -1;
            int minBurst = Integer.MAX_VALUE;

            // Find the process with the shortest burst time among all arrived processes
            for (int i = 0; i < n; i++) {
                if (processes[i].arrivalTime <= currentTime && !processes[i].isCompleted) {
                    if (processes[i].burstTime < minBurst) {
                        minBurst = processes[i].burstTime;
                        shortestJob = i;
                    }

                    // If burst times are equal, choose the one that arrived earlier
                    else if (processes[i].burstTime == minBurst) {
                        if (processes[i].arrivalTime < processes[shortestJob].arrivalTime) {
                            shortestJob = i;
                        }
                    }
                }
            }

            if (shortestJob == -1) {
                // No process available, increment time
                currentTime++;
            } else {
                // Execute the shortest job first
                processes[shortestJob].completionTime = currentTime + processes[shortestJob].burstTime;
                processes[shortestJob].turnaroundTime = processes[shortestJob].completionTime - processes[shortestJob].arrivalTime;
                processes[shortestJob].waitingTime = processes[shortestJob].turnaroundTime - processes[shortestJob].burstTime;

                processes[shortestJob].isCompleted = true;
                completed++;
                currentTime = processes[shortestJob].completionTime;
            }
        }
    }


    public void displaySchedulingResults() {
        String header = String.format(
                "%-8s %-15s %-12s %-20s %-15s %-15s%n",
                "Process", "Arrival Time", "Burst Time", "Completion Time", "Turnaround Time", "Waiting Time"
        );
        StringBuilder table = new StringBuilder(header);
        table.append("------------------------------------------------------------------------------------------\n");

        for (Process2 p : processes) {
            table.append(String.format("%-8d %-15d %-12d %-20d %-15d %-15d%n",
                    p.pid,
                    p.arrivalTime,
                    p.burstTime,
                    p.completionTime,
                    p.turnaroundTime,
                    p.waitingTime
                    )
            );
        }

        System.out.println(table);

        double avgWaitingTime = 0, avgTurnaroundTime = 0;
        for (Process2 p: processes) {
            avgWaitingTime += n;
            avgTurnaroundTime += n;
        }
        avgWaitingTime /= n;
        avgTurnaroundTime /= n;

        System.out.printf("\nAverage Waiting Time: %.2f", avgWaitingTime);
        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTurnaroundTime);
    }

    public static void main(String[] args) {
        Process2[] processes = {
                new Process2(1, 0, 6),
                new Process2(2, 2, 4),
                new Process2(3, 4, 2),
                new Process2(4, 6, 3)
        };

        SJFScheduler scheduler = new SJFScheduler(processes);

        // Run scheduling algorithm
        scheduler.schedule();

        scheduler.displaySchedulingResults();
    }
}
