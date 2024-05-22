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

    public Cluster(Integer num, Double minX, double maxX, double minY, double maxY) {
        this.num = num;
        points = new HashSet<>();
        centroid = new Point("", minX + (maxX - minX) * Math.random(), minY + (maxY - minY) * Math.random());
    }

    public void addPoint(Point P) {
        points.add(P);
    }

    public void clearPoints() {
        points.clear();
    }

    public Integer getNum() {
        return num;
    }

    public Set<Point> getPoints() {
        return points;
    }

    public Point getCentroid() {
        return centroid;
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
        Set<Point> data = new HashSet<>();
        Double maxX = Double.MIN_VALUE;
        Double maxY = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        while ((line = br.readLine()) != null) {
            String[] str = line.split(",");
            Double x = Double.parseDouble(str[1]);
            Double y = Double.parseDouble(str[2]);
            if (x > maxX)
                maxX = x;
            if (x < minX)
                minX = x;
            if (y > maxY)
                maxY = y;
            if (y < minY)
                minY = y;
            data.add(new Point(str[0], x, y));
        }
        A2_G6_t1 hello = new A2_G6_t1(Pts, data, minX, maxX, minY, maxY);
        hello.Update_clusters(); // 사용법. update_cluster 이외에 딴거 조작할 필요 없다
        br.close();
        long endTime = System.currentTimeMillis();
        System.out.println("A2_G6_t1 Processing Execution time: " + (endTime - startTime) / 1000.0);

    }

    public Integer pts;
    public Set<Point> points;
    public List<Cluster> clusters;

    public A2_G6_t1(Integer Pts, Set<Point> data, Double minX, double maxX, double minY, double maxY) {
        this.pts = Pts;
        this.points = data;
        this.clusters = new ArrayList<>(Pts);
        for (Integer i = 0; i < Pts; i++) {
            this.clusters.add(new Cluster(i, minX, maxX, minY, maxY));
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
        this.clusters.get(cluster_num).addPoint(p);
        return;
    }

}
