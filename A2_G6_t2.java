import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class A2_G6_t2 {
    public static void main(String[] args) throws FileNotFoundException {

        // Argument Parsing: java A2_G6_t2 ./database/artd-31.csv 5 0.5
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

        DBSCAN dbscan = new DBSCAN(fileName, distanceMetric);
        dbscan.readCSV();


        if (checkEstimatedBoth){ // Extra works
            int estimatedMinPts = 4;  // 2-dim
            minPts = estimatedMinPts;
            dbscan.setMinPts(minPts);

            eps = dbscan.estimateEps();
            dbscan.setEps(eps);
            System.out.println("Estimated eps: " + eps);

            minPts = dbscan.evalMinPts();
            dbscan.setMinPts(minPts);
            System.out.println("Estimated MinPts: " + minPts);
        }
        else if (checkEstimatedMinPts){
            dbscan.setEps(eps);

            // Finding optimal minPts
            int estimatedMinPts = dbscan.evalMinPts();  // Need to find optimal MinPts
            minPts = estimatedMinPts;
            dbscan.setMinPts(minPts);
            System.out.println("Estimated MinPts: " + minPts);

        }
        else if (checkEstimatedEps){
            dbscan.setMinPts(minPts);

            eps = dbscan.estimateEps();
            dbscan.setEps(eps);
            System.out.println("Estimated eps: " + eps);
        }
        else {
            dbscan.setMinPts(minPts);
            dbscan.setEps(eps);
        }

        long startTime = System.currentTimeMillis();
        dbscan.scan();
        long endTime = System.currentTimeMillis();

        dbscan.printConfig();
        dbscan.printClusters();
        // System.out.println("A2_G6_t2 Processing Execution time: " + (endTime - startTime)/1000.0);

//        if (storeResult) {
//            String resultFileName = "./A2_G6_t2_analysis/D1_EpsMean.csv";
//            try {
//                dbscan.storeResults(resultFileName);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

    }

}

class Point {
    String id;
    double x, y;
    int groundTruthLabel;
    int predictedLabel = 0;  // -1 for noise, 0 for unpredicted label, and 1 ~ n for predicted label
    boolean classified = false;

    // Constructor
    public Point(String id, double x, double y, int groundTruth) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.groundTruthLabel = groundTruth;
    }
}

class DBSCAN {
    String fileName;
    File file;
    int minPts;
    double eps;
    List<Point> points = new ArrayList<>();

    String distanceMetric;

    int clusterId = 1; // Starting out clusterId with 1
    List<List<Point>> clusters = new ArrayList<>();

    // Constructor
    public DBSCAN(String fileName, String distanceMetric) {
        this.fileName = fileName;
        this.distanceMetric = distanceMetric;
    }

    public void setMinPts(int minPts) {
        this.minPts = minPts;
    }

    public void setEps(double eps) {
        this.eps = eps;
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

    public void printConfig() {
        System.out.println("Number of clusters : " + (this.clusterId - 1));

        int cntNoise = 0;
        for (Point point : this.points) {
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

    public void storeResults(String resultFileName) throws IOException {
        FileWriter w1 = new FileWriter(resultFileName);

        for (Point point : points) {
           // w1.write(point.id + "," + point.x + "," + point.y + "," + point.groundTruthLabel + "," + point.predictedLabel + "\n");
            w1.write(point.id + "," + point.x + "," + point.y + "," + point.predictedLabel + "\n");
        }
        w1.close();
    }

    public int evalMinPts() {
        // Set default estimatedEps
        int estimatedEps = 0;

        // Finding 0.5 Eps neighbor points
        Map<Integer, Integer> newMap = new HashMap<>();
        for (Point point : this.points) {
            List<Point> halfNeighbor = halfRegionQuery(point);
            if (halfNeighbor.size() > 1) newMap.put(halfNeighbor.size(), newMap.getOrDefault(halfNeighbor.size(), 0) + 1);
        }

        // Calculate Total sum
        int sum = 0;
        for (int key : newMap.keySet()) {
            if (key == 1) continue;
            sum += newMap.get(key);
        }

        // Get median idx
        int median = (sum % 2 == 1) ? sum / 2 + 1 : sum / 2;

        // Get estimatedEps from median by comparing partialSum > median (partialSum is initialized with 0)
        // estimatedEps = key - 1 (due to neighbor points have point itself.)
        int partialSum = 0;
        for (int key : newMap.keySet()) {
            if (key == 1) continue;
            partialSum += newMap.get(key);
            if (partialSum > median) {
                estimatedEps = key - 1;
                break;
            }
        }

        return estimatedEps;
    }

    public void scan() {
        for (Point point : this.points) {
            if (!point.classified) {
                point.classified = true;
                if (expandCluster(point)) {
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

    private List<Point> halfRegionQuery(Point refPoint) {
        List<Point> neighbors = new ArrayList<>();
        for (Point trgPoint : this.points) {
            if (calcDistance(refPoint, trgPoint) <= 0.5 * this.eps) {
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
        if (this.distanceMetric.equals("Euclidean")) {
            return calcEuclideanDistance(p1, p2);
        } else if (this.distanceMetric.equals("Manhatten")) {
            return calcManhattenDistance(p1, p2);
        } else if (this.distanceMetric.equals("Chebyshev")) {
            return calcChebyshevDistance(p1, p2);
        } else if (this.distanceMetric.equals("Minkowski")) {
            return calcMinkowskiDistance(p1, p2, 3.0);
        }
        return -1.0;
    }

    private double calcEuclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow((p1.x - p2.x), 2) + Math.pow((p1.y - p2.y), 2));
    }

    private double calcManhattenDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    private double calcChebyshevDistance(Point p1, Point p2) {
        return Math.max(Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
    }

    private double calcMinkowskiDistance(Point p1, Point p2, double p) {
        return Math.pow(Math.pow(Math.abs(p1.x - p2.x), p) + Math.pow(Math.abs(p1.y - p2.y), p), 1/p);
    }

    // Estimating optimal eps using k-distance graph heuristic method


   // FINAL VERSION
    public double estimateEps() {
        List<Double> kDistances = new ArrayList<>();
        for (Point p1 : points) {
            List<Double> distances = new ArrayList<>();
            for (Point p2 : points) {
                if (!p1.equals(p2)) {
                    distances.add(calcDistance(p1, p2));
                }
            }
            Collections.sort(distances);
            double first_Distance = distances.get(minPts - 1);
            Point the_point = null;

            for (Point p2 : points) {
                if (calcDistance(p1, p2) == first_Distance) {
                    the_point = p2;
                    break;
                }
            }

            if (the_point != null) {
                List<Double> secondDistances = new ArrayList<>();
                for (Point p3 : points) {
                    if (!p1.equals(p3) && !the_point.equals(p3)) {
                        secondDistances.add(calcDistance(the_point, p3));
                    }
                }

                Collections.sort(secondDistances);
                double second_Distance = secondDistances.get(minPts - 1);

                double kDistance = Math.max(first_Distance, second_Distance);
                kDistances.add(kDistance);
            }
        }

        Collections.sort(kDistances);

        double median;
        int size = kDistances.size();
        if (size % 2 == 0) {
            median = (kDistances.get(size / 2 - 1) + kDistances.get(size / 2)) / 2.0;
        } else {
            median = kDistances.get(size / 2);
        }

        double eps = findKneePoint(kDistances);

        double diff1 = Math.abs(eps - median);
        double diff2 = Math.abs(kDistances.get(0) - median);

        if (10* diff2 <=  diff1) {
            eps = median;
        }

        return eps;
    }


    private double findKneePoint(List<Double> sortedDistances) {
        int nPoints = sortedDistances.size();
        double maxDistance = 0.0;
        int kneePoint = 0;
        List<Double> perpendicularDistances = new ArrayList<>();

        for (int i = 0; i < nPoints; i++) {
            double distance = perpendicularDistance(sortedDistances, i);
            perpendicularDistances.add(distance);
            if (distance > maxDistance) {
                maxDistance = distance;
                kneePoint = i;
            }
        }

        return sortedDistances.get(kneePoint);
    }

    private double perpendicularDistance(List<Double> sortedDistances, int i) {
        double x1 = 0;
        double y1 = sortedDistances.get(0);
        double x2 = sortedDistances.size() - 1;
        double y2 = sortedDistances.get(sortedDistances.size() - 1);

        double x0 = i;
        double y0 = sortedDistances.get(i);

        double numerator = Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1);
        double denominator = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));

        return numerator / denominator;
    }

}
