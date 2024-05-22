import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class A2_G6_t1 {
    public static void main(String[] args) throws FileNotFoundException {

        // Argument Parsing: java A2_G2_t1 ./database/artd-31.csv 15

        String fileName = args[0];
        Integer Pts = 1;
        if (args.length == 2) {
            Pts = Integer.parseInt(args[1]);
        } else {
            Pts = 10; // have to estimate
        }

        long startTime = System.currentTimeMillis();

        long endTime = System.currentTimeMillis();
        System.out.println("A2_G6_t1 Processing Execution time: " + (endTime - startTime) / 1000.0);

    }

    public Integer Pts;
    public List<Point> Centroids;
    public List<Set<Point>> Cluster;
    public A2_G6_t1(Integer Pts) { // initialize class
        this.Pts = Pts;
        this.Centroids = Centroids;
        this.Cluster = Cluster;
     }

    public class Point {
        private Double x;
        private Double y;
        private Integer cluster_number;

        public Point(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        public Double getX() {
            return this.x;
        }

        public Double getY() {
            return this.y;
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
    
    // 클러스터 다시 분류하기 구현 
    public void Update_clusters() {
        for(Set<Point> cluster : Cluster) {
            for(Point p : cluster) {
                Find_nearest_centroid(p);
            }
        }
        Integer now_cluster = 0;
        Set<Point> changed_Points = new HashSet<>();
        for(Set<Point> cluster : Cluster) {
            now_cluster++;
            for(Point p : cluster) {
                Integer new_cluster = p.getC();
                if(now_cluster != new_cluster) 
                    cluster.remove(p);
                    changed_Points.add(p);
            }
        }
        now_cluster = 0;
        for(Set<Point> cluster : Cluster) {
            now_cluster++;
            for(Point p : changed_Points) {
                if(now_cluster == p.getC()) {
                    cluster.add(p);
                }
            }
        }
        return;
    }
    
    public void Update_centroids() {
        Integer size = Cluster.size();
        Integer centroid_num = 0;
        for(Set<Point> cluster : Cluster) {
            centroid_num++;
            Double new_x = 0.d;
            Double new_y = 0.d;
            for(Point p : cluster) {
                new_x += p.x / size;
                new_y += p.y / size;
            }
            Centroids.set(centroid_num, new Point(new_x, new_y));
        }
        return;
    }

    public void Find_nearest_centroid(Point p) {
        Double distance = Double.MAX_VALUE;
        Integer cluster_num = 0;
        for(Point centroid : Centroids) {
            cluster_num++;
            if(distance > p.Distance(centroid)) {
                distance = p.Distance(centroid);
                p.setC(cluster_num);
            }
        }
        return;
    }

    public void Centroid_distance(Point p, Set<Point> centroids) {
        Double distance = Double.MAX_VALUE;
        Integer s = 0;
        for (Point i : centroids) {
            s++;
            if(distance > i.Distance(p)) {
                distance = i.Distance(p);
                p.setC(s);
            }
        }
        return;
    }
}