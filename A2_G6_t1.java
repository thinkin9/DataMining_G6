import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Point {
    private String name;
    private Double x;
    private Double y;
    private Integer cluster_number;

    public Point(String name, Double x, Double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.cluster_number = -1;
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

    public Double Inner_Avg_distance(Point p, Integer s) {
        if (s <= 1)
            return 0.0d;
        Double distance = 0.0d;
        for (Point c : points) {
            distance += p.Distance(c);
        }
        distance = (Double) (distance / (s - 1));
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
        while ((line = br.readLine()) != null) {
            String[] str = line.split(",");
            Double x = Double.parseDouble(str[1]);
            Double y = Double.parseDouble(str[2]);
            data.add(new Point(str[0], x, y));
        }
        A2_G6_t1 hello = new A2_G6_t1(Pts, data);
        hello.Update_clusters();
        br.close();
        long endTime = System.currentTimeMillis();
        System.out.println("A2_G6_t1 Processing Execution time: " + (endTime - startTime) / 1000.0);

        Double silhouetteIndex = hello.Calculate_silhouettes();
        System.out.println("Silhouette Index: " + silhouetteIndex);
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

        Double dist;
        for (Integer i = 1; i < Pts; i++) {
            Double totalDistance = 0.0d;
            List<Double> distances = new ArrayList<>(Pts);
            for (Integer j = 0; j < points.size(); j++) {
                dist = nowc.Distance(points.get(j));
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
                    this.clusters.add(new Cluster(0, nowc));
                    System.out.println(nowc.getX());
                    break;
                }
            }
        }
    }

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
        this.clusters.get(cluster_num).removePoint(p);
        this.clusters.get(cluster_num).addPoint(p);
        return;
    }

    public Integer Find_second_nearest_cluster(Point p) {
        Double min = Double.MAX_VALUE;
        Integer ans = p.getC();
        for (Cluster c : this.clusters) {
            if (c.getNum() != p.getC() && min > p.Distance(c.getCentroid())) {
                min = p.Distance(c.getCentroid());
                ans = c.getNum();
            }
        }
        return ans;
    }

    public Double Calculate_silhouettes() {
        Double silhouette = 0.0d;
        Double silhouette_coef_a = 0.0d;
        Double silhouette_coef_b = 0.0d;
        for (Point p : this.points) {
            Integer now_cluster = p.getC();
            Integer second_nearest = Find_second_nearest_cluster(p);
            silhouette_coef_b = this.clusters.get(second_nearest).Outter_Avg_distance(p,
                    clusters);
            silhouette_coef_a = this.clusters.get(now_cluster).Inner_Avg_distance(p,
                    clusters.get(now_cluster).getSize());
            if (silhouette_coef_b > silhouette_coef_a) {
                silhouette += ((silhouette_coef_b - silhouette_coef_a) / (silhouette_coef_b) / points.size());
            } else {
                silhouette += ((silhouette_coef_b - silhouette_coef_a) / (silhouette_coef_a) / points.size());
            }
        }
        return silhouette;
    }
}
