import java.util.Queue;
import java.util.LinkedList;

class Process {
    int pid;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    public Process(int pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

class RoundRobinScheduler {
    private Process[] processes;
    private int n;
    private int currentTime;
    private int quantum;
    private Queue<Process> readyQueue;

    public RoundRobinScheduler(Process[] processes, int quantum) {
        this.processes = processes;
        this.n = processes.length;
        this.currentTime = 0;
        this.quantum = quantum;
        this.readyQueue = new LinkedList<>();
    }

    public void schedule() {
        int completed = 0;

        while (completed != n) {
            // Check for new arrivals and add them to the ready queue
            for (int i = 0; i < n; i++) {
                if (processes[i].arrivalTime <= currentTime &&
                        processes[i].remainingTime > 0) {
                    readyQueue.offer(processes[i]);
                }
            }

            if (readyQueue.isEmpty()) {
                // No process is ready, increment current time
                currentTime++;
            } else {
                // Execute the process at the head of the ready queue
                Process currentProcess = readyQueue.poll();
                int executionTime = Math.min(quantum, currentProcess.remainingTime);
                currentProcess.remainingTime -= executionTime;
                currentTime += executionTime;

                if (currentProcess.remainingTime == 0) {
                    // Process has completed
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    completed++;
                } else {
                    // Process still has remaining time, add it back to the queue
                    readyQueue.offer(currentProcess);
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
        table.append("-------------------------------------------------------------------------------------------------------------\n");

        for (Process p : processes) {
            table.append(String.format("%-8d %-15d %-12d %-20d %-15d %-15d%n",
                    p.pid,
                    p.arrivalTime,
                    p.burstTime,
                    p.completionTime,
                    p.turnaroundTime,
                    p.waitingTime)
            );
        }

        System.out.println(table);

        double avgWaitingTime = 0, avgTurnaroundTime = 0;
        for (Process p: processes) {
            avgWaitingTime += n;
            avgTurnaroundTime += n;
        }
        avgWaitingTime /= n;
        avgTurnaroundTime /= n;

        System.out.printf("\nAverage Waiting Time: %.2f", avgWaitingTime);
        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTurnaroundTime);
    }

    public static void main(String[] args) {
        // Create sample processes (pid, arrivalTime, burstTime)
        Process[] processes = {
                new Process(1, 0, 6),
                new Process(2, 2, 4),
                new Process(3, 4, 2),
                new Process(4, 6, 3)
        };

        int quantum = 2; // Time quantum
        RoundRobinScheduler scheduler = new RoundRobinScheduler(processes, quantum);
        scheduler.schedule();
        scheduler.displaySchedulingResults();
    }
}