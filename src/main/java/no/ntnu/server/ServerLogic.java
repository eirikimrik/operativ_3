package no.ntnu.server;

import java.util.HashMap;
import java.util.Map;

public class ServerLogic {

    private Map<String, Double> results;

    public ServerLogic() {
        this.results = new HashMap<>();
    }

    public void handleMessage(String message) {
        String[] messageParts = message.split(", ");
        for (String s : messageParts) {
            if (s.startsWith("F")) {
                int iterations = Integer.parseInt(s.substring(1));
                double result = fibonacciRecursive(iterations);
                results.put(s, result);
            } else {
                throw new IllegalArgumentException("Invalid operation: " + s);
            }
        }
    }

    private double fibonacciRecursive(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number of iterations must be positive.");
        }
        if (n == 1 || n == 2) {
            return 1;
        }
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }

    public void decompileMessage(Map<Integer, String> map, String message) {
        String[] command = message.split("P");
        int priority = Integer.parseInt(command[1].split(" ")[0].substring(1));

        map.put(priority, command[0].split(" ")[0]);
    }

    public Map<String, Double> getResult(String message) {
        handleMessage(message);
        Map<String, Double> result = new HashMap<>(this.results);
        this.results.clear();
        return result;
    }

}
