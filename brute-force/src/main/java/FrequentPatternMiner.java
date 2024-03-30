import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FrequentPatternMiner {

    public void findAndPrintFrequentPatterns(String fileName, double threshold) throws IOException {
        List<String> transactions = readTransactions(fileName);
        List<Character> uniqueItems = findUniqueItems(transactions);
        Map<String, Integer> frequentPatterns = findFrequentPatterns(transactions, uniqueItems, threshold);
        System.out.println("Frequent Patterns:");
        for (String pattern : frequentPatterns.keySet()) {
            System.out.println(pattern + " : " + frequentPatterns.get(pattern));
        }
    }

    private List<String> readTransactions(String fileName) throws IOException {
        List<String> transactions = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            transactions.add(line.trim());
        }
        reader.close();
        return transactions;
    }

    private List<Character> findUniqueItems(List<String> transactions) {
        Set<Character> uniqueItemsSet = new HashSet<>();
        for (String transaction : transactions) {
            char[] items = transaction.replaceAll("\\s+", "").toCharArray();
            for (char item : items) {
                if (item != ',') { // Ignore comma
                    uniqueItemsSet.add(item);
                }
            }
        }
        return new ArrayList<>(uniqueItemsSet);
    }

    private Map<String, Integer> findFrequentPatterns(List<String> transactions, List<Character> uniqueItems, double threshold) {
        Map<String, Integer> patternCount = new HashMap<>();

        // Generate all possible subsets of unique items
        int n = uniqueItems.size();
        for (int i = 1; i < (1 << n); i++) {
            StringBuilder subsetBuilder = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    subsetBuilder.append(uniqueItems.get(j));
                }
            }
            String subset = subsetBuilder.toString();

            // Count occurrences of subset in transactions
            for (String transaction : transactions) {
                boolean found = true;
                for (char item : subset.toCharArray()) {
                    if (item != ',' && transaction.indexOf(item) == -1) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    int count = patternCount.getOrDefault(subset, 0);
                    patternCount.put(subset, count + 1);
                }
            }
        }

        // Filter patterns based on threshold
        Map<String, Integer> frequentPatterns = new HashMap<>();
        int transactionCount = transactions.size();
        for (Map.Entry<String, Integer> entry : patternCount.entrySet()) {
            double support = (double) entry.getValue() / transactionCount;
            if (support >= threshold) {
                frequentPatterns.put(entry.getKey(), entry.getValue());
            }
        }

        return frequentPatterns;
    }
}
