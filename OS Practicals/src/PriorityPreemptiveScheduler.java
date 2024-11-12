class Process5 {
    int pid;
    int arrivalTime;
    int burstTime;
    int priority;        // Lower number means higher priority
    int remainingTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    public Process5(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }
}

public class PriorityPreemptiveScheduler {
    private Process5[] processes;
    private int n;
    private int currentTime;
    private Process5 runningProcess;

    public PriorityPreemptiveScheduler(Process5[] processes) {
        this.processes = processes;
        this.n = processes.length;
        this.currentTime = 0;
        this.runningProcess = null;
    }

    public void schedule() {
        int completed = 0;

        while (completed != n) {
            int highestPriorityIndex = findHighPriorityProcess();

            if (highestPriorityIndex != -1 &&
                    (runningProcess == null ||
                            processes[highestPriorityIndex].priority < runningProcess.priority)) {
                // Preempt the running process if a higher priority one is available
                if (runningProcess != null) {
                    runningProcess.remainingTime -= currentTime - runningProcess.arrivalTime;
                }

                runningProcess = processes[highestPriorityIndex];
            }

            if (runningProcess == null) {
                currentTime++;
            } else {
                runningProcess.remainingTime--;
                currentTime++;

                if (runningProcess.remainingTime == 0) {
                    // Process has completed
                    runningProcess.completionTime = currentTime;
                    runningProcess.turnaroundTime = runningProcess.completionTime - runningProcess.arrivalTime;
                    runningProcess.waitingTime = runningProcess.turnaroundTime - runningProcess.burstTime;

                    completed++;
                    runningProcess = null;
                }
            }

        }
    }

    private int findHighPriorityProcess() {
        int highestPriorityIndex = -1;
        int highestPriority = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            if (processes[i].arrivalTime <= currentTime && processes[i].remainingTime > 0) {
                if (processes[i].priority < highestPriority) {
                    highestPriorityIndex = i;
                }

                else if (processes[i].priority == processes[highestPriorityIndex].priority
                        && processes[i].arrivalTime < processes[highestPriorityIndex].arrivalTime) {
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

        for (Process5 p : processes) {
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
        for (Process5 p: processes) {
            avgWaitingTime += n;
            avgTurnaroundTime += n;
        }
        avgWaitingTime /= n;
        avgTurnaroundTime /= n;

        System.out.printf("\nAverage Waiting Time: %.2f", avgWaitingTime);
        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTurnaroundTime);
    }

    public static void main(String[] args) {
        Process5[] processes = {
                new Process5(1, 0, 4, 2),
                new Process5(2, 1, 3, 1),  // Highest priority (lowest number)
                new Process5(3, 2, 1, 3),
                new Process5(4, 3, 5, 2)
        };

        PriorityPreemptiveScheduler scheduler = new PriorityPreemptiveScheduler(processes);
        scheduler.schedule();
        scheduler.displaySchedulingResults();
    }
}
