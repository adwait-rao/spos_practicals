class Process1 {
    int pid;
    int arrivalTime;
    int burstTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    public Process1(int pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }
}

public class FCFSScheduler {
    private Process1[] processes;
    private int n;

    public FCFSScheduler(Process1[] processes) {
        this.processes = processes;
        this.n = processes.length;
    }

    private void sortByArrivalTime() {
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (processes[j].arrivalTime > processes[j + 1].arrivalTime) {
                    Process1 temp = processes[j];
                    processes[j] = processes[j + 1];
                    processes[j + 1] = temp;
                }
            }
        }
    }

    public void schedule() {
        // sort processes by arrival time
        sortByArrivalTime();

        // calculate completion time for the first process
        processes[0].completionTime = processes[0].arrivalTime + processes[0].burstTime;

        // calculate completion time for the rest of the processes
        for (int i = 1; i < n; i++) {
            if (processes[i].arrivalTime > processes[i - 1].completionTime) {
                processes[i].completionTime = processes[i].arrivalTime + processes[i].burstTime;
            } else {
                processes[i].completionTime = processes[i - 1].completionTime + processes[i].burstTime;
            }
        }


        // calculate turnaround time and waiting time for all processes
        for (int i = 0; i < n; i++) {
            processes[i].turnaroundTime = processes[i].completionTime - processes[i].arrivalTime;
            processes[i].waitingTime = processes[i].turnaroundTime - processes[i].burstTime;
        }
    }

    public void displaySchedulingResults() {
        String header = String.format(
                "%-8s %-15s %-12s %-20s %-15s %-15s%n",
                "Process", "Arrival Time", "Burst Time", "Completion Time", "Turnaround Time", "Waiting Time"
        );
        StringBuilder table = new StringBuilder(header);
        table.append("-------------------------------------------------------------------------------------------------------------\n");

        for (Process1 p : processes) {
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
        for (Process1 p: processes) {
            avgWaitingTime += n;
            avgTurnaroundTime += n;
        }
        avgWaitingTime /= n;
        avgTurnaroundTime /= n;

        System.out.printf("\nAverage Waiting Time: %.2f", avgWaitingTime);
        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTurnaroundTime);
    }

    public static void main(String[] args) {
        Process1[] proesses = {
                new Process1(1, 0, 6),
                new Process1(2, 2, 4),
                new Process1(3, 4, 2),
                new Process1(4, 6, 3)
        };

        FCFSScheduler scheduler = new FCFSScheduler(proesses);

        scheduler.schedule();

        scheduler.displaySchedulingResults();
    }
}
