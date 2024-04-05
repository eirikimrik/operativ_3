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
            if (s.startsWith("A")) {
                String[] factors = s.substring(1).split(" ");
                double result = Double.parseDouble(factors[0]) + Double.parseDouble(factors[1]);
                results.put(s , result);
            } else if (s.startsWith("S")) {
                String[] factors = s.substring(1).split(" ");
                double result = Double.parseDouble(factors[0]) - Double.parseDouble(factors[1]);
                results.put(s, result);
            } else if (s.startsWith("M")) {
                String[] factors = s.substring(1).split(" ");
                double result = Double.parseDouble(factors[0]) * Double.parseDouble(factors[1]);
                results.put(s, result);
            } else if (s.startsWith("D")) {
                String[] factors = s.substring(1).split(" ");
                double result = Double.parseDouble(factors[0]) / Double.parseDouble(factors[1]);
                results.put(s, result);
            } else {
                throw new IllegalArgumentException("Invalid operation: " + s);
            }
        }
    }

    public Map<String, Double> getResult(String message) {
        handleMessage(message);
        Map<String, Double> result = new HashMap<>(this.results);
        this.results.clear();
        return result;
    }

}
