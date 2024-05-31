import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import java.io.BufferedWriter;
import java.io.FileWriter;

//class representing one data point
class Point {
    private String name; // point name
    private Double x;
    private Double y;
    private Integer cluster_number; // Which cluster this point belongs to

    public Point(String name, Double x, Double y, Integer c) { // initialize class
        this.name = name;
        this.x = x;
        this.y = y;
        this.cluster_number = c;
    }

    public String getName() {
        return this.name;
    }

    public Double getX() {
        return this.x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return this.y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getC() {
        return this.cluster_number;
    }

    public void setC(Integer C) {
        this.cluster_number = C;
    }

    public Double Distance(Point a) { // calculate distance between this point and other point
        return Math.sqrt((a.x - this.x) * (a.x - this.x) + (a.y - this.y) * (a.y - this.y));
    }
}

// class representing one cluster with data points and centroid
class Cluster {
    private Integer num; // numbering
    private Set<Point> points; // points belong to this cluster
    private Point centroid; // centroid

    public Cluster(Integer num, Point centroid) { // initialize class
        this.num = num;
        this.points = new HashSet<>();
        this.centroid = centroid;
    }

    public void addPoint(Point P) {
        this.points.add(P);
    }

    public void clearPoints() { // remove all points in Cluster but centroid information is not changed
        this.points.clear();
    }

    public Integer getNum() {
        return num;
    }

    public Set<Point> getPoints() {
        return this.points;
    }

    public Point getCentroid() {
        return centroid;
    }

    public Integer getSize() {
        return this.points.size();
    }

    public void Update_centroid() { // recalculate the centroid of this cluster
        Integer size = this.points.size();
        if (size == 0) // avoid dividing to 0
            return;
        Double new_x = 0.0d;
        Double new_y = 0.0d;
        for (Point p : this.points) { // calculate the new centroid position
            new_x += (p.getX() / size);
            new_y += (p.getY() / size);
        }
        this.centroid = new Point(this.centroid.getName(), new_x, new_y, this.centroid.getC()); // update centroid
    }

    public Double Inner_Avg_distance(Point p, Cluster currentCluster) { // calculate a(i) for silhouette index
        if (currentCluster.getSize() <= 1) // special case: size is 0 or 1
            return 0.0d;
        Double distance = 0.0d;
        for (Point c : currentCluster.getPoints()) { // sum distance between p and all points in current cluster
            distance += p.Distance(c);
        }
        distance = (Double) (distance / (currentCluster.getSize() - 1)); // divide to size-1
        return distance;
    }

    public Double Outter_Avg_distance(Point p, List<Cluster> clusters) { // calculate b(i) for silhouette index
        Double minDistance = Double.MAX_VALUE; // set to max double initially to find minimum
        for (Cluster c : clusters) {
            if (c.getSize() == 0) // special case: size is 0
                continue;
            if (!c.equals(this)) { // check if c is not itself
                Double distance = 0.0d;
                for (Point point : c.getPoints()) { // sum distance between p and all points in cluster c
                    distance += p.Distance(point);
                }
                distance /= c.getPoints().size();
                if (distance < minDistance) { // find cluster with minimum avg distance
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }

}

public class A2_G6_t1 {
    public static void main(String[] args) throws IOException {

        // Argument Parsing: java A2_G2_t1 ./database/artd-31.csv 15
        Integer Pts = -1;
        Integer mode; // mode = 0: k specified, mode = 1: k not specified.
        if (args.length == 2) {
            Pts = Integer.parseInt(args[1]);
            mode = 0;
        } else {
            mode = 1;
        }

        long startTime = System.currentTimeMillis(); // check execution time

        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line;
        List<Point> data = new ArrayList<>();
        while ((line = br.readLine()) != null) { // read data and store into List of Points
            String[] str = line.split(",");
            Double x = Double.parseDouble(str[1]);
            Double y = Double.parseDouble(str[2]);
            data.add(new Point(str[0], x, y, -1));
        }
        br.close();

        Integer t = 100; // maximum number of loop repetition per run
        Integer repeat = 10; // run amount per k value
        Double silhouetteIndex = -1.0d; // initial setting, if there is better value for some run, it will be updated.

        String outputFilename = "kmeans_results.csv"; // for visualizing purpose

        List<Cluster> Result = new ArrayList<>();

        if (mode == 0) {
            for (int j = 0; j < repeat; j++) { // repeat repeat times
                A2_G6_t1 run = new A2_G6_t1(Pts, data); // initialize
                Double tmp = run.run_and_return_silhouette(t);
                if (silhouetteIndex < tmp) { // if this run has better silhouette index
                    silhouetteIndex = tmp;
                    Result.clear();
                    Result = run.getClusters(); // update result
                    run.save_CSV(outputFilename, Result); // save clustering result into csv file
                }
            }
        } else if (mode == 1) {
            Integer[] test = { 2, 4, 8, 16, 32, 64 }; // k values to test
            for (int i = 0; i < test.length; i++) {
                Integer nowPts = test[i];
                for (int j = 0; j < repeat; j++) { // repeat repeat times for each k
                    A2_G6_t1 run = new A2_G6_t1(nowPts, data);
                    Double tmp = run.run_and_return_silhouette(t);
                    if (silhouetteIndex < tmp) { // if this run has better silhouette index
                        silhouetteIndex = tmp;
                        Pts = nowPts; // update k with best result
                        Result.clear();
                        Result = run.getClusters(); // update clustering result
                        run.save_CSV(outputFilename, Result); // save clustering result into csv file
                    }
                }
            }
            Integer Pts1 = Pts; // selected k
            for (int nowPts = Pts1 / 2 + 1; nowPts < Pts1 * 2 - 1; nowPts++) { // repeat from k=k/2+1 to 2k-1
                for (int j = 0; j < repeat; j++) {
                    A2_G6_t1 run = new A2_G6_t1(nowPts, data);
                    Double tmp = run.run_and_return_silhouette(t);
                    if (silhouetteIndex < tmp) { // if this run has better silhouette index
                        silhouetteIndex = tmp;
                        Pts = nowPts; // update k with best result
                        Result.clear();
                        Result = run.getClusters(); // update clustering result
                        run.save_CSV(outputFilename, Result); // save clustering result into csv file
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis(); // check execution time
        // System.out.println("A2_G6_t1 Processing Execution time: " + (endTime -
        // startTime) / 1000.0);
        // System.out.println("Silhouette Index: " + silhouetteIndex); //debugging
        // purposes
        if (mode == 0) {
        } else if (mode == 1) { // estimated k
            System.out.println("estimated k: " + Pts);
        }
        for (Cluster c : Result) { // print clustering result
            List<Point> pts = new ArrayList<>();
            for (Point pt : c.getPoints()) {
                pts.add(pt);
            }
            pts.sort((pt1, pt2) -> { // sort with point number to print
                int num1 = Integer.parseInt(pt1.getName().substring(1));
                int num2 = Integer.parseInt(pt2.getName().substring(1));
                return Integer.compare(num1, num2);
            });
            System.out.print("Cluster #" + (c.getNum() + 1) + " => ");
            for (Point pt : pts) {
                System.out.print(pt.getName() + " ");
            }
            System.out.println();
        }
    }

    private Integer pts; // k
    private List<Point> points; // all data points
    private List<Cluster> clusters; // clusters

    public List<Cluster> getClusters() { // for printing purpose
        return this.clusters;
    }

    public A2_G6_t1(Integer Pts, List<Point> data) { // initialize centroids: k-means++
        this.pts = Pts;
        this.points = data;
        this.clusters = new ArrayList<>(Pts);
        Random random = new Random();
        Integer c = random.nextInt(this.points.size()); // select initial point number
        Point nowc = this.points.get(c); // get point information
        this.clusters.add(new Cluster(0, nowc)); // add to first cluster's centroid.

        // Double dist;
        for (int i = 1; i < Pts; i++) {
            Double totalDistance = 0.0d; // sum of distances
            List<Double> distances = new ArrayList<>(this.points.size()); // array that save distances for each point
            for (int j = 0; j < this.points.size(); j++) { // for each point
                Double dist = Double.MAX_VALUE; // set to max double initially to find minimum
                for (Cluster cluster : this.clusters) { // find closest centroid (minimum distance) for each point
                    Double tmp = this.points.get(j).Distance(cluster.getCentroid());
                    dist = Double.min(dist, tmp);
                }
                distances.add(j, dist * dist); // add square of distance to array
                totalDistance += (dist * dist); // and totalDistance.
            }
            double r = random.nextDouble() * totalDistance; // randomly choose value between 0 and totalDistance
            double currDist = 0.0; // current sum
            for (int j = 0; j < this.points.size(); j++) { // this part chooses next centroid.
                currDist += distances.get(j); // restore info
                if (currDist >= r) { // choose centroids with probability proportional to square of the distances
                    c = j;
                    nowc = this.points.get(c); // get point information
                    this.clusters.add(new Cluster(i, nowc)); // add to i-1th cluster's centroid.
                    break;
                }
            }
        }
    }
    /*
     * public void print_cluster() {
     * for(Cluster c : clusters) {
     * String s = "cluster: ";
     * for(Point p : c.getPoints()) {
     * s += "p" + p.get_point_num() + ", ";
     * }
     * System.out.println(s);
     * }
     * }
     */

    public void Update_clusters() { // inside the loop.. one step.
        for (Cluster cluster : this.clusters) {
            cluster.clearPoints();
        }
        for (Point p : this.points) {
            Find_nearest_centroid(p);
        }
        for (Cluster cluster : this.clusters) {
            cluster.Update_centroid();
        }
    }

    public Double run_and_return_silhouette(Integer t) { // run k-means++. t is the maximum number of loop repetition.
        for (int i = 0; i < t; i++) {
            Set<Double> old_clusters_x = new HashSet<>(this.pts);// for checking convergence. (check centroids)
            Set<Double> old_clusters_y = new HashSet<>(this.pts);
            Set<Double> new_clusters_x = new HashSet<>(this.pts);
            Set<Double> new_clusters_y = new HashSet<>(this.pts);
            for (int j = 0; j < this.pts; j++) {
                old_clusters_x.add(this.clusters.get(j).getCentroid().getX());
                old_clusters_y.add(this.clusters.get(j).getCentroid().getY());
            }
            Update_clusters();
            for (int j = 0; j < this.pts; j++) {
                new_clusters_x.add(this.clusters.get(j).getCentroid().getX());
                new_clusters_y.add(this.clusters.get(j).getCentroid().getY());
            }
            if (old_clusters_x.equals(new_clusters_x) && old_clusters_y.equals(new_clusters_y)) { // check convergence
                break;
            }
        }
        return Calculate_silhouettes();
    }

    public void Find_nearest_centroid(Point p) { // find nearest centroid for given point
        Double distance = Double.MAX_VALUE; // set to max double initially to find minimum
        Integer cluster_num = -1;
        for (Cluster c : this.clusters) { // loop for clusters
            Point cp = c.getCentroid();
            if (distance > p.Distance(cp)) {
                distance = p.Distance(cp);
                cluster_num = c.getNum();
            }
        }
        p.setC(cluster_num); // set this point's cluster number
        // this.clusters.get(cluster_num).removePoint(p);
        this.clusters.get(cluster_num).addPoint(p);
        return;
    }

    public Double Calculate_silhouettes() { // calculate silhouette value with each point's a(p) b(p) values
        Double silhouette = 0.0d;
        for (Point p : this.points) {
            Cluster currentCluster = this.clusters.get(p.getC());
            Double a = currentCluster.Inner_Avg_distance(p, currentCluster);
            Double b = currentCluster.Outter_Avg_distance(p, this.clusters);
            silhouette += (b - a) / Math.max(a, b);
        }
        return silhouette / this.points.size();
    }

    public void save_CSV(String filename, List<Cluster> cs) throws IOException { // save result to csv file. this file
                                                                                 // includes name of the point, point's
                                                                                 // coordinates, and the cluster number
                                                                                 // sorted by this algorithm.
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("name,x,y,cluster\n");
        Integer cnum = 0;
        for (Cluster c : cs) {
            cnum++;
            for (Point point : c.getPoints()) {
                writer.write(point.getName() + "," + point.getX() + "," + point.getY() + "," + point.getC() + "\n");
            }
        }
        writer.close();
    }
}