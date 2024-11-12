class Process4 {
    int pid;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    public Process4(int pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime; // initially, remaining time equals burst time
    }
}

public class SRTFScheduler {
    private Process4[] processes;
    private int n;

    public SRTFScheduler(Process4[] processes) {
        this.processes = processes;
        this.n = processes.length;
    }

    public void schedule() {
        int currentTime = 0;
        int completed = 0;

        while (completed != n) {
            int shortestJob = -1;
            int minRemaining = Integer.MAX_VALUE;

            // Find process with the shortest remaining time among arrived processes
            for (int i = 0; i < n; i++) {
                if (processes[i].arrivalTime <= currentTime && processes[i].remainingTime > 0) {
                    if (processes[i].remainingTime < minRemaining) {
                        minRemaining = processes[i].remainingTime;
                        shortestJob = i;
                    }
                }

                else if (processes[i].remainingTime == minRemaining &&
                        processes[i].arrivalTime < processes[shortestJob].arrivalTime) {
                    shortestJob = i;
                }
            }

            if (shortestJob == -1) {
                currentTime++;
            } else {
                // execute process for 1 time unit
                processes[shortestJob].remainingTime--;
                currentTime++;

                // if process completes
                if (processes[shortestJob].remainingTime == 0) {
                    completed++;
                    processes[shortestJob].completionTime = currentTime;
                    processes[shortestJob].turnaroundTime =
                            processes[shortestJob].completionTime
                                    - processes[shortestJob].arrivalTime;
                    processes[shortestJob].waitingTime =
                            processes[shortestJob].turnaroundTime
                                    - processes[shortestJob].burstTime;
                }
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

        for (Process4 p : processes) {
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
        for (Process4 p: processes) {
            avgWaitingTime += n;
            avgTurnaroundTime += n;
        }
        avgWaitingTime /= n;
        avgTurnaroundTime /= n;

        System.out.printf("\nAverage Waiting Time: %.2f", avgWaitingTime);
        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTurnaroundTime);
    }

    public static void main(String[] args) {
        Process4[] processes = {
                new Process4(1, 0, 6),
                new Process4(2, 2, 4),
                new Process4(3, 4, 2),
                new Process4(4, 6, 3)
        };

        SRTFScheduler scheduler = new SRTFScheduler(processes);
        scheduler.schedule();
        scheduler.displaySchedulingResults();
    }
}
