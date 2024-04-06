package no.ntnu.process;

import java.util.*;

public class CPU_Scheduling {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int n = scanner.nextInt();

        ArrayList<Process> processes = new ArrayList<>();


        for (int i = 0; i < n; i++) {
            System.out.println("Enter details for Process " + (i + 1));
            System.out.print("Process ID: ");
            int pid = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            processes.add(new Process(pid, arrivalTime, burstTime, 0));
        }


        Collections.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));


        calculateFCFS(processes);


        double avgWaitingTimeFCFS = calculateAverageWaitingTime(processes);
        double avgTurnaroundTimeFCFS = calculateAverageTurnaroundTime(processes);

        System.out.println("\nFCFS Scheduling:");
        System.out.println("Average Waiting Time: " + avgWaitingTimeFCFS);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTimeFCFS);


        processes.clear();
        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for Process " + (i + 1));
            System.out.print("Process ID: ");
            int pid = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Priority: ");
            int priority = scanner.nextInt();

            processes.add(new Process(pid, arrivalTime, burstTime, priority));
        }


        Collections.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));


        calculatePreemptivePriority(processes);


        double avgWaitingTimePP = calculateAverageWaitingTime(processes);
        double avgTurnaroundTimePP = calculateAverageTurnaroundTime(processes);

        System.out.println("\nPreemptive Priority Scheduling:");
        System.out.println("Average Waiting Time: " + avgWaitingTimePP);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTimePP);
    }

    private static void calculateFCFS(ArrayList<Process> processes) {
        int currentTime = 0;
        for (Process process : processes) {
            if (process.arrivalTime > currentTime)
                currentTime = process.arrivalTime;
            process.waitingTime = currentTime - process.arrivalTime;
            process.turnaroundTime = process.waitingTime + process.burstTime;
            currentTime += process.burstTime;
        }
    }

    private static void calculatePreemptivePriority(ArrayList<Process> processes) {
        int currentTime = 0;
        PriorityQueue<Process> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority));
        int i = 0;
        while (i < processes.size() || !priorityQueue.isEmpty()) {
            while (i < processes.size() && processes.get(i).arrivalTime <= currentTime) {
                priorityQueue.add(processes.get(i));
                i++;
            }
            if (priorityQueue.isEmpty()) {
                currentTime = processes.get(i).arrivalTime;
                continue;
            }
            Process currentProcess = priorityQueue.poll();
            currentProcess.waitingTime += currentTime - currentProcess.arrivalTime;
            currentProcess.turnaroundTime = currentProcess.waitingTime + currentProcess.burstTime;
            currentTime += currentProcess.burstTime;
            while (i < processes.size() && processes.get(i).arrivalTime <= currentTime) {
                priorityQueue.add(processes.get(i));
                i++;
            }
            if (!priorityQueue.isEmpty()) {
                Process nextProcess = priorityQueue.peek();
                if (nextProcess.priority < currentProcess.priority)
                    priorityQueue.add(currentProcess);
                else
                    priorityQueue.add(nextProcess);
            }
        }
    }

    private static double calculateAverageWaitingTime(ArrayList<Process> processes) {
        double totalWaitingTime = 0;
        for (Process process : processes) {
            totalWaitingTime += process.waitingTime;
        }
        return totalWaitingTime / processes.size();
    }

    private static double calculateAverageTurnaroundTime(ArrayList<Process> processes) {
        double totalTurnaroundTime = 0;
        for (Process process : processes) {
            totalTurnaroundTime += process.turnaroundTime;
        }
        return totalTurnaroundTime / processes.size();
    }
}
