import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class A2_G6_t2 {
    public static void main(String[] args) throws FileNotFoundException {

        // Argument Parsing: java A2_G2_t2 ./database/artd-31.csv 5 0.5
        String fileName;
        int minPts = 0;
        double eps = 0.0;
        boolean storeResult = true;

        int argc = args.length;
        boolean checkEstimatedBoth = false;
        boolean checkEstimatedMinPts = false;
        boolean checkEstimatedEps = false;

        if (argc == 1){
            fileName = args[0];
            checkEstimatedBoth = true;
        }
        else if (argc == 2){
            fileName = args[0];
            if (args[1].contains(".")){
                checkEstimatedMinPts = true;
                eps = Double.parseDouble(args[1]);
            }
            else {
                checkEstimatedEps = true;
                minPts = Integer.parseInt(args[1]);
            }
        }
        else if (argc == 3){
            fileName = args[0];
            minPts = Integer.parseInt(args[1]);
            eps = Double.parseDouble(args[2]);
        }
        else {
            throw new IllegalArgumentException("Check the number of arguments: " + argc);
        }

        String[] distanceMetrics = {"Euclidean", "Manhatten", "Chebyshev", "Minkowski"};
        String distanceMetric = distanceMetrics[0]; // Set Distance Metric

        if (checkEstimatedBoth){ // Extra works
            int estimatedMinPts = 4;  // Need to find optimal MinPts
            minPts = estimatedMinPts;
            System.out.println("Estimated MinPts: " + minPts);

            double estimatedEps = 0.5; // Need to find optimal Eps
            eps = estimatedEps;
            System.out.println("Estimated eps: " + eps);
        }
        else if (checkEstimatedMinPts){
            int estimatedMinPts = 4;  // Need to find optimal MinPts
            minPts = estimatedMinPts;
            System.out.println("Estimated MinPts: " + minPts);
        }
        else if (checkEstimatedEps){
            double estimatedEps = 0.5; // Need to find optimal Eps
            eps = estimatedEps;
            System.out.println("Estimated eps: " + eps);
        }

        DBSCAN dbscan = new DBSCAN(fileName, minPts, eps, distanceMetric);
        dbscan.readCSV();

        long startTime = System.currentTimeMillis();
        dbscan.scan();
        long endTime = System.currentTimeMillis();

        dbscan.printConfig();
        dbscan.printClusters();

        System.out.println("A2_G6_t2 Processing Execution time: " + (endTime - startTime)/1000.0);

        if (storeResult) {
            String groundLabelFileName = "./A2_G6_t2_analysis/" + "e" + String.valueOf(eps).substring(2) + "m" + String.valueOf(minPts) + "groundTruth.txt";
            String predictedLabelFileName = "./A2_G6_t2_analysis/" + "e" + String.valueOf(eps).substring(2) + "m" + String.valueOf(minPts) + "Predicted.txt";

            try {
                dbscan.storeResults(groundLabelFileName, predictedLabelFileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Point {
    String      id;
    double      x, y;
    int         groundTruthLabel;
    int         predictedLabel = 0;  // -1 for noise, 0 for unpredicted label, and 1 ~ n for predicted label
    boolean     classified = false;


    // Constructor
    public Point(String id, double x, double y, int groundTruth) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.groundTruthLabel = groundTruth;
    }
}

class DBSCAN{
    String      fileName;
    File        file;
    int         minPts;
    double      eps;
    List<Point> points = new ArrayList<>();

    String      distanceMetric;

    int         clusterId = 1; // Starting out clusterId with 1
    List<List<Point>> clusters = new ArrayList<>();

    // Constructor
    public DBSCAN(String fileName, int minPts, double eps, String distanceMetric) {
        this.fileName = fileName;
        this.minPts = minPts;
        this.eps = eps;
        this.distanceMetric = distanceMetric;
    }

    // Parse the dataset file (.csv)
    public void readCSV() throws FileNotFoundException {
        try {
            this.file = new File(this.fileName);
            Scanner scanner = new Scanner(this.file);

            while (scanner.hasNextLine()) {
                String[] items = scanner.nextLine().split(",");

                String id = items[0];
                double x = Double.parseDouble(items[1]);
                double y = Double.parseDouble(items[2]);
                int groundTruth = Integer.parseInt(items[3]);

                this.points.add(new Point(id, x, y, groundTruth));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void printConfig(){
        System.out.println("Number of clusters : " + (this.clusterId - 1));

        int cntNoise = 0;
        for (Point point : this.points){
            if (point.predictedLabel == -1) cntNoise++;
        }
        System.out.println("Number of noise : " + cntNoise);
    }

    public void printClusters() {
        for (int i = 0; i < this.clusters.size(); i++) {
            System.out.print("Cluster #" + (i + 1) + " => ");
            List<Point> cluster = this.clusters.get(i);
            Collections.sort(cluster, (p1, p2) -> Integer.compare(Integer.parseInt(p1.id.substring(1)), Integer.parseInt(p2.id.substring(1))));
            for (int j = 0; j < cluster.size(); j++) {
                if (j > 0) System.out.print(" ");
                System.out.print(cluster.get(j).id);
            }
            System.out.println();
        }
    }

    public void storeResults(String groundLabelFileName, String predictedLabelFileName) throws IOException {
        FileWriter w1 = new FileWriter(groundLabelFileName);
        FileWriter w2 = new FileWriter(predictedLabelFileName);

        for (Point point : points) {
            w1.write(point.groundTruthLabel + "\n");
            w2.write(point.predictedLabel + "\n");
        }
        w1.close();
        w2.close();
    }

    public void scan(){
        for (Point point : this.points) {
            if (!point.classified) {
                point.classified = true;
                if (expandCluster(point)){
                    clusterId++;
                }
            }
        }
    }

    private List<Point> regionQuery(Point refPoint) {
        List<Point> neighbors = new ArrayList<>();
        for (Point trgPoint : this.points) {
            if (calcDistance(refPoint, trgPoint) <= this.eps) {
                neighbors.add(trgPoint);
            }
        }
        return neighbors;
    }

    private boolean expandCluster(Point point){
        List<Point> seeds = regionQuery(point);

        if (seeds.size() < this.minPts) {
            point.predictedLabel = -1;  // -1 for noise
            return false;
        }
        else {
            List<Point> cluster = new ArrayList<>();
            point.classified = true;
            point.predictedLabel = this.clusterId;
            cluster.add(point);

            while(!seeds.isEmpty()){
                Point nowPoint = seeds.get(0);
                List<Point> result = regionQuery(nowPoint);
                if (result.size() >= this.minPts) {
                    for(Point nxtPoint : result){
                        // Unclassified
                        if (!nxtPoint.classified){
                            seeds.add(nxtPoint);
                            nxtPoint.classified = true;
                            nxtPoint.predictedLabel = this.clusterId;
                            cluster.add(nxtPoint);
                        }
                        // Noise
                        else if(nxtPoint.predictedLabel == -1){
                            nxtPoint.predictedLabel = this.clusterId;
                            cluster.add(nxtPoint);
                        }

                    }
                }
                seeds.remove(nowPoint);
            }
            clusters.add(cluster);
            return true;
        }
    }

    private double calcDistance(Point p1, Point p2) {
        switch (distanceMetric) {
            case "Euclidean":
                return euclideanDistance(p1, p2);
            case "Manhatten":
                return manhattenDistance(p1, p2);
            case "Chebyshev":
                return chebyshevDistance(p1, p2);
            case "Minkowski":
                return minkowskiDistance(p1, p2, 3);
            default:
                throw new IllegalArgumentException("Illegal distanceMetric: " + distanceMetric);
        }
    }

    private double euclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    private double manhattenDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    private double chebyshevDistance(Point p1, Point p2) {
        return Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
    }

    private double minkowskiDistance(Point p1, Point p2, int p) {
        return Math.pow(Math.pow(Math.abs(p1.x - p2.x), p) + Math.pow(Math.abs(p1.y - p2.y), p), 1.0 / p);
    }
}


