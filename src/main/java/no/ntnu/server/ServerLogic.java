package no.ntnu.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Server logic.
 */
public class ServerLogic {

    private Map<String, Double> results;

    /**
     * Server logic.
     */
    public ServerLogic() {
        this.results = new HashMap<>();
    }

    /**
     * Handles a message.
     * @param message message.
     */
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

    /**
     * Returns the result of the process.
     *
     * @param message message.
     * @return the result of the process.
     */
    public Map<String, Double> getResult(String message) {
        handleMessage(message);
        Map<String, Double> result = new HashMap<>(this.results);
        this.results.clear();
        return result;
    }

}
