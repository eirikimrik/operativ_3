package no.ntnu.server;

import java.util.HashMap;
import java.util.Map;

/**
 * The ServerLogic class handles the processing of messages received from clients.
 */
public class ServerLogic {

    private Map<String, Double> results;

    /**
     * Constructor for ServerLogic class
     */
    public ServerLogic() {
        this.results = new HashMap<>();
    }

    /**
     * Handles a message received from a client by parsing the message and performing
     * the specified operations.
     *
     * @param message The message received from the client.
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

    /**
     * Calculates the fibonacci number in a recursive method..
     *
     * @param n The index of the Fibonacci number to calculate.
     * @return The value of the nth Fibonacci number.
     */
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
     * Decompiles a message received from a client into its components and updates the provided map.
     * @param map     The map to update with the priority and operation name pair extracted from the message.
     * @param message The message received from the client in the format "<operation> Priority <priority>".
     * @return The operation name extracted from the message.
     */
    public String decompileMessage(Map<Integer, String> map, String message) {
        String[] command = message.split("P");
        int priority = Integer.parseInt(command[1].split(" ")[0]);

        map.put(priority, command[0].split(" ")[0]);
        return command[0].split(" ")[0];
    }

    /**
     * Parses the message received from a client and returns the result of the
     * operations performed.
     *
     * @param message The message received from the client.
     * @return A map containing the results of the operations.
     */
    public Map<String, Double> getResult(String message) {
        handleMessage(message);
        Map<String, Double> result = new HashMap<>(this.results);
        this.results.clear();
        return result;
    }

}
