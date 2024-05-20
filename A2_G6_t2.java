import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class A2_G6_t2 {
    public static void main(String[] args) throws FileNotFoundException {

        // Argument Parsing: java A2_G2_t2 ./database/artd-31.csv 5 0.5

        String fileName = args[0];
        int minPts = Integer.parseInt(args[1]);
        double eps = Double.parseDouble(args[2]);

        long startTime = System.currentTimeMillis();

        long endTime = System.currentTimeMillis();
        System.out.println("A2_G6_t2 Processing Execution time: " + (endTime - startTime)/1000.0);

    }
}

class DBSCAN{
    private File         file;
    private int          minPts;
    private double       eps;

}


