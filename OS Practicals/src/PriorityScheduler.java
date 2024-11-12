class Process3 {
    int pid;
    int arrivalTime;
    int burstTime;
    int priority;
    int completionTime;
    int turnaroundTime;
    int waitingTime;
    boolean isCompleted;

    public Process3(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.isCompleted = false;
    }
}

public class PriorityScheduler {
    private Process3[] processes;
    private int n;
    private int currentTime;

    public PriorityScheduler(Process3[] processes) {
        this.processes = processes;
        this.n = processes.length;
        this.currentTime = 0;
    }

    public void schedule() {
        int completed = 0;

        while (completed != n) {
            int highestPriorityIndex = findHighestPriorityProcess();

            if (highestPriorityIndex == -1) {
                // No process available at current time
                currentTime++;
            } else {
                Process3 currentProcess = processes[highestPriorityIndex];

                // Execute the process (non-preemptive)
                currentProcess.completionTime = currentTime + currentProcess.burstTime;
                currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;

                currentProcess.isCompleted = true;
                completed++;
                currentTime = currentProcess.completionTime;
            }
        }
    }

    private int findHighestPriorityProcess() {
        int highestPriorityIndex = -1;
        int highestPriority = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            if (processes[i].arrivalTime <= currentTime && !processes[i].isCompleted) {
                // select process with the highest priority (lowest priority number)
                if (processes[i].priority < highestPriority) {
                    highestPriorityIndex = i;
                }

                // if priorities are equal, choose the one that arrived first
                else if (processes[i].priority == highestPriority &&
                        processes[i].arrivalTime < processes[highestPriorityIndex].arrivalTime) {
                    highestPriorityIndex = i;
                }
            }
        }
        return highestPriorityIndex;
    }


    public void displaySchedulingResults() {
        String header = String.format(
                "%-8s %8s %-15s %-12s %-20s %-15s %-15s%n",
                "Process", "Priority", "Arrival Time", "Burst Time", "Completion Time", "Turnaround Time", "Waiting Time"
        );
        StringBuilder table = new StringBuilder(header);
        table.append("------------------------------------------------------------------------------------------\n");

        for (Process3 p : processes) {
            table.append(String.format("%-8d %-8d %-15d %-12d %-20d %-15d %-15d%n",
                            p.pid,
                            p.priority,
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
        for (Process3 p: processes) {
            avgWaitingTime += n;
            avgTurnaroundTime += n;
        }
        avgWaitingTime /= n;
        avgTurnaroundTime /= n;

        System.out.printf("\nAverage Waiting Time: %.2f", avgWaitingTime);
        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTurnaroundTime);
    }

    public static void main(String[] args) {
        Process3[] processes = {
                new Process3(1, 0, 4, 2),
                new Process3(2, 1, 3, 1),  // Highest priority (lowest number)
                new Process3(3, 2, 1, 3),
                new Process3(4, 3, 5, 2)
        };

        PriorityScheduler scheduler = new PriorityScheduler(processes);
        scheduler.schedule();
        scheduler.displaySchedulingResults();
    }
}
