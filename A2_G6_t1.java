import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class A2_G6_t1 {
    public static void main(String[] args) throws FileNotFoundException {

        // Argument Parsing: java A2_G2_t1 ./database/artd-31.csv 15

        String fileName = args[0];
        int minPts = Integer.parseInt(args[1]);
        double eps = Double.parseDouble(args[2]);
        
        long startTime = System.currentTimeMillis();

        long endTime = System.currentTimeMillis();
        System.out.println("A2_G6_t1 Processing Execution time: " + (endTime - startTime)/1000.0);

    }
}
