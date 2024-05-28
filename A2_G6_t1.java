import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import java.io.BufferedWriter;
import java.io.FileWriter;

class Point {
    private String name;
    private Double x;
    private Double y;
    private Integer cluster_number;

    public Point(String name, Double x, Double y, Integer c) {
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

    public Double Distance(Point a) {
        return Math.sqrt((a.x - this.x) * (a.x - this.x) + (a.y - this.y) * (a.y - this.y));
    }
}

class Cluster {
    private Integer num;
    private Set<Point> points;
    private Point centroid;

    public Cluster(Integer num, Point centroid) {
        this.num = num;
        this.points = new HashSet<>();
        this.centroid = centroid;
    }

    public void addPoint(Point P) {
        this.points.add(P);
    }

    public void removePoint(Point P) {
        this.points.remove(P);
    }

    public void clearPoints() {
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

    public void Update_centroid() {
        Integer size = this.points.size();
        if (size == 0)
            return;
        Double new_x = 0.0d;
        Double new_y = 0.0d;
        for (Point p : this.points) {
            new_x += (p.getX() / size);
            new_y += (p.getY() / size);
        }
        this.centroid = new Point(this.centroid.getName(), new_x, new_y, this.centroid.getC());
    }

    public Double Inner_Avg_distance(Point p, Cluster currentCluster) {
        if (currentCluster.getSize() <= 1)
            return 0.0d;
        Double distance = 0.0d;
        for (Point c : currentCluster.getPoints()) {
            distance += p.Distance(c);
        }
        distance = (Double) (distance / (currentCluster.getSize() - 1));
        return distance;
    }

    public Double Outter_Avg_distance(Point p, List<Cluster> clusters) {
        Double minDistance = Double.MAX_VALUE;
        for (Cluster c : clusters) {
            if (!c.equals(this)) {
                Double distance = 0.0d;
                for (Point point : c.getPoints()) {
                    distance += p.Distance(point);
                }
                distance /= c.getPoints().size();
                if (distance < minDistance) {
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
        Integer mode;
        if (args.length == 2) {
            Pts = Integer.parseInt(args[1]);
            mode = 0;
        } else {
            mode = 1;
        }

        long startTime = System.currentTimeMillis();

        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line;
        List<Point> data = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] str = line.split(",");
            Double x = Double.parseDouble(str[1]);
            Double y = Double.parseDouble(str[2]);
            data.add(new Point(str[0], x, y, -1));
        }
        br.close();

        Integer t = 15; // how much update_cluster is executed per run
        Integer repeat = 10; // run amount
        Double silhouetteIndex = -1.0d;

        String outputFilename = "kmeans_results.csv";

        List<Cluster> Result = new ArrayList<>();

        if (mode == 0) {
            A2_G6_t1 run = new A2_G6_t1(Pts, data);
            silhouetteIndex = run.run_and_return_silhouette(t);
            Result = run.getClusters();
        } else if (mode == 1) {
            Integer[] test = { 2, 4, 8, 16, 32, 64 };
            for (int i = 0; i < test.length; i++) {
                Integer nowPts = test[i];
                for (int j = 0; j < repeat; j++) {
                    A2_G6_t1 run = new A2_G6_t1(nowPts, data);
                    Double tmp = run.run_and_return_silhouette(t);
                    if (silhouetteIndex < tmp) {
                        silhouetteIndex = tmp;
                        Pts = nowPts;
                        Result.clear();
                        Result = run.getClusters();
                        run.save_CSV(outputFilename, Result);
                    }
                }
            }
            Integer Pts1 = Pts;
            for (int nowPts = Pts1 / 2 + 1; nowPts < Pts1 * 3 / 2 - 1; nowPts++) {
                for (int j = 0; j < repeat; j++) {
                    A2_G6_t1 run = new A2_G6_t1(nowPts, data);
                    Double tmp = run.run_and_return_silhouette(t);
                    if (silhouetteIndex < tmp) {
                        silhouetteIndex = tmp;
                        Pts = nowPts;
                        Result.clear();
                        Result = run.getClusters();
                        run.save_CSV(outputFilename, Result);
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("A2_G6_t1 Processing Execution time: " + (endTime - startTime) / 1000.0);
        // System.out.println("Silhouette Index: " + silhouetteIndex);
        if (mode == 0) {
        } else if (mode == 1) {
            System.out.println("estimated k: " + Pts);
        }
        for (Cluster c : Result) {
            List<Point> pts = new ArrayList<>();
            for (Point pt : c.getPoints()) {
                pts.add(pt);
            }
            pts.sort((pt1, pt2) -> {
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

    private Integer pts;
    private List<Point> points;
    private List<Cluster> clusters;

    public List<Cluster> getClusters() {
        return this.clusters;
    }

    public A2_G6_t1(Integer Pts, List<Point> data) {
        this.pts = Pts;
        this.points = data;
        this.clusters = new ArrayList<>(Pts);
        Random random = new Random();
        Integer c = random.nextInt(this.points.size());
        Point nowc = this.points.get(c);
        this.clusters.add(new Cluster(0, nowc));

        // Double dist;
        for (int i = 1; i < Pts; i++) {
            Double totalDistance = 0.0d;
            List<Double> distances = new ArrayList<>(this.points.size());
            for (int j = 0; j < this.points.size(); j++) {
                Double dist = Double.MAX_VALUE;
                for (Cluster cluster : this.clusters) {
                    Double tmp = this.points.get(j).Distance(cluster.getCentroid());
                    dist = Double.min(dist, tmp);
                }
                distances.add(j, dist * dist);
                totalDistance += (dist * dist);
            }
            double r = random.nextDouble() * totalDistance;
            double currDist = 0.0;
            for (int j = 0; j < this.points.size(); j++) {
                currDist += distances.get(j);
                boolean flag = true;
                for (Cluster k : this.clusters) {
                    if (k.getCentroid().getX() == this.points.get(j).getX() && k.getCentroid().getY() == this.points
                            .get(j).getY()) {
                        flag = false;
                    }
                }
                if (flag && currDist >= r) {
                    c = j;
                    nowc = this.points.get(c);
                    this.clusters.add(new Cluster(i, nowc));
                    break;
                }
            }
        }
    }
    /*
     * public void pirnt_cluster() {
     * for(Cluster c : clusters) {
     * String s = "cluster: ";
     * for(Point p : c.getPoints()) {
     * s += "p" + p.get_point_num() + ", ";
     * }
     * System.out.println(s);
     * }
     * }
     */

    public void Update_clusters() {
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

    public Double run_and_return_silhouette(Integer t) {
        for (int i = 0; i < t; i++) {
            List<Point> old_clusters = new ArrayList<>(this.pts);
            List<Point> new_clusters = new ArrayList<>(this.pts);
            for (int j = 0; j < this.pts; j++) {
                old_clusters.add(j, this.clusters.get(j).getCentroid());
            }
            Update_clusters();
            for (int j = 0; j < this.pts; j++) {
                old_clusters.add(j, this.clusters.get(j).getCentroid());
            }
            if (old_clusters == new_clusters) {
                System.out.println(i);
                break;
            }
        }
        return Calculate_silhouettes();
    }

    public void Find_nearest_centroid(Point p) {
        Double distance = Double.MAX_VALUE;
        Integer cluster_num = -1;
        for (Cluster c : this.clusters) {
            Point cp = c.getCentroid();
            if (distance > p.Distance(cp)) {
                distance = p.Distance(cp);
                cluster_num = c.getNum();
            }
        }
        p.setC(cluster_num);
        // this.clusters.get(cluster_num).removePoint(p);
        this.clusters.get(cluster_num).addPoint(p);
        return;
    }

    public Double Calculate_silhouettes() {
        Double silhouette = 0.0d;
        for (Point p : this.points) {
            Cluster currentCluster = this.clusters.get(p.getC());
            Double a = currentCluster.Inner_Avg_distance(p, currentCluster);
            Double b = currentCluster.Outter_Avg_distance(p, this.clusters);
            silhouette += (b - a) / Math.max(a, b);
        }
        return silhouette / this.points.size();
    }

    public void save_CSV(String filename, List<Cluster> cs) throws IOException {
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