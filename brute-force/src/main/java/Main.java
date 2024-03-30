import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Main <file_name> <threshold>");
            return;
        }

        String fileName = args[0];
        double threshold = Double.parseDouble(args[1]);

        long startTime = System.currentTimeMillis();
        try {
            FrequentPatternMiner frequentPatternMiner = new FrequentPatternMiner();
            frequentPatternMiner.findAndPrintFrequentPatterns(fileName, threshold);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("FPTree Processing Execution time: " + (endTime - startTime)/1000.0);
    }
}
