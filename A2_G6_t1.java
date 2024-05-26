import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Point {
    private String name;
    private Double x;
    private Double y;
    private Integer cluster_number;
    private Integer point_number;

    public Point(String name, Double x, Double y, Integer num) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.cluster_number = -1;
        this.point_number = num;
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

    public Integer get_point_num() {
        return this.point_number;
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
        points = new HashSet<>();
        this.centroid = centroid;
    }

    public void addPoint(Point P) {
        points.add(P);
    }

    public void removePoint(Point P) {
        points.remove(P);
    }

    public void clearPoints() {
        points.clear();
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
        for (Point p : points) {
            new_x += p.getX() / size;
            new_y += p.getY() / size;
        }
        this.centroid.setX(new_x);
        this.centroid.setY(new_y);
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
        Integer Pts = 1;
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
        Integer point_num = 0;
        while ((line = br.readLine()) != null) {
            point_num++;
            String[] str = line.split(",");
            Double x = Double.parseDouble(str[1]);
            Double y = Double.parseDouble(str[2]);
            data.add(new Point(str[0], x, y, point_num));
        }
        A2_G6_t1 hello = new A2_G6_t1(Pts, data);
        hello.Update_clusters();
        br.close();
        long endTime = System.currentTimeMillis();
        System.out.println("A2_G6_t1 Processing Execution time: " + (endTime - startTime) / 1000.0);

        Double silhouetteIndex = hello.Calculate_silhouettes();
        System.out.println("Silhouette Index: " + silhouetteIndex);
        //hello.pirnt_cluster();
    }

    public Integer pts;
    public List<Point> points;
    public List<Cluster> clusters;

    public A2_G6_t1(Integer Pts, List<Point> data) {
        this.pts = Pts;
        this.points = data;
        this.clusters = new ArrayList<>(Pts);
        Random random = new Random();
        Integer c = 4500; // random.nextInt(Pts);
        Point nowc = points.get(c);
        this.clusters.add(new Cluster(0, nowc));

        //Double dist;
        for (Integer i = 1; i < Pts; i++) {
            Double dist = Double.MAX_VALUE;
            Double totalDistance = 0.0d;
            List<Double> distances = new ArrayList<>(Pts);
            for (Integer j = 0; j < points.size(); j++) {
                for(Cluster cluster : clusters) {
                    Double tmp = points.get(j).Distance(cluster.getCentroid());
                    dist = Double.min(dist, tmp);
                }
                //dist = nowc.Distance(points.get(j));
                distances.add(j, dist * dist);
                totalDistance += dist * dist;
            }
            double r = random.nextDouble() * totalDistance;
            double currDist = 0.0;
            for (Integer j = 0; j < points.size(); j++) {
                currDist += distances.get(j);
                if (currDist >= r) {
                    c = j;
                    nowc = points.get(c);
                    this.clusters.add(new Cluster(i, nowc));
                    System.out.println(nowc.getX());
                    break;
                }
            }
        }
    }
    /* 
    public void pirnt_cluster() {
        for(Cluster c : clusters) {
            String s = "cluster: ";
            for(Point p : c.getPoints()) {
                s += "p" + p.get_point_num() + ", ";
            }
            System.out.println(s);
        }
    }
    */

    public void Update_clusters() {
        for (Cluster cluster : clusters) {
            cluster.clearPoints();
        }
        for (Point p : this.points) {
            Find_nearest_centroid(p);
        }
        for (Cluster cluster : clusters) {
            cluster.Update_centroid();
        }
    }

    public void Find_nearest_centroid(Point p) {
        Double distance = Double.MAX_VALUE;
        Integer cluster_num = 0;
        for (Cluster c : this.clusters) {
            Point cp = c.getCentroid();
            if (distance > p.Distance(cp)) {
                distance = p.Distance(cp);
                cluster_num = c.getNum();
            }
        }
        p.setC(cluster_num);
        //this.clusters.get(cluster_num).removePoint(p);
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
}
