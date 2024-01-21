import java.security.InvalidAlgorithmParameterException;
import java.io.FileInputStream;
import java.util.*;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 *
 */
public class GraphProcessor {
    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */
    private List<Point> graphinf; 
    private Map <Point, Integer> conMap;
    private Map<Point, List<Point>> alist;
    private int edgetot;
    private int verttot;

    // What other instance variables do I need?





    public void initialize(FileInputStream file) throws Exception {
        // TODO: Implement initialize
        Scanner reader = new Scanner(file);
// exception
        if (!reader.hasNextInt()){
            reader.close();
            throw new Exception("Could not read .graph file");
        }

        String[] readver = reader.nextLine().split(" ");

        verttot = Integer.parseInt(readver[0]);
        edgetot = Integer.parseInt(readver[1]);
// making up graph

        graphinf = new ArrayList<>();

        for(int i = 0; i < verttot; i++){
            String[] readline = reader.nextLine().split(" ");
            graphinf.add(new Point(Double.parseDouble(readline[1]), Double.parseDouble(readline[2])));
        }
// adjacency list
        alist = new HashMap<>();
        for(int i = 0; i < edgetot; i++){
            String[] readline = reader.nextLine().split(" ");
            Point a = graphinf.get(Integer.parseInt(readline[0]));
            Point b = graphinf.get(Integer.parseInt(readline[1]));
            if(!alist.containsKey(a)){
                alist.put(a, new ArrayList<>());
            }
            if(!alist.containsKey(b)){
                alist.put(b, new ArrayList<>());
            }
            alist.get(a).add(b);
            alist.get(b).add(a);
        }

        Connect();

        reader.close();

    }

    private void Connect(){
        conMap = new HashMap<>();
        Set<Point> visited = new HashSet<>();
        int comp = 0;
        for (Point point1: graphinf){
            if (visited.contains(point1)){
                continue;
            }

            Stack<Point> toExplore = new Stack<>();
            Point current;
            toExplore.add(point1);
            visited.add(point1);
            conMap.put(point1, comp);
            while (!toExplore.isEmpty()){
                current = toExplore.pop();
                for (Point neigh : alist.get(current)){
                    if (!visited.contains(neigh)){
                        visited.add(neigh);
                        conMap.put(neigh, comp);
                        toExplore.push(neigh);
                    }
                }
            }
            comp += 1;
        }
    }


    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        // TODO: Implement nearestPoint
        double distnear = 100000000;
        Point pointnear = graphinf.get(0);

        for(Point poin: graphinf){
            if(distnear > p.distance(poin)){
                distnear = p.distance(poin);
                pointnear = poin;
            }
        }
        return pointnear;
    }


    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        // TODO Implement routeDistance
        double tot = 0.0;
        for (int i = 0; i < route.size()-1; i++){
            tot += route.get(i).distance(route.get(i+1));
        }
        return tot;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        if (conMap.containsKey(p1) && conMap.containsKey(p2)){
            if (conMap.get(p1) == conMap.get(p2)){
                return true;
            }
        }
        return false;
    }


    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        // TODO: Implement route
        List<Point> fin = new ArrayList<>();
        HashSet<Point> visted = new HashSet<>();
        HashMap<Point, Double> disbetwn = new HashMap<>();
        HashMap<Point, Point> pathtrvld = new HashMap<>();

        Comparator<Point> compare = (a,b) -> {double fir = disbetwn.get(a); double sec = disbetwn.get(b); 
            return Double.compare(fir, sec);};
        PriorityQueue<Point> needexploring = new PriorityQueue<>(compare);

        if (!alist.containsKey(end) || !alist.containsKey(start)){
            throw new InvalidAlgorithmParameterException("No path between start and end");
        }

        disbetwn.put(start, 0.0);
        needexploring.add(start);

        while(!needexploring.isEmpty()){
            Point cur = needexploring.remove();
            if(cur.equals(end)){
                break;
            }
            visted.add(cur);
            List<Point> vals = alist.get(cur);
            for (Point neighbor : vals) {
                
                if (visted.contains(neighbor)) continue;
                double newDist = disbetwn.getOrDefault(cur,Double.MAX_VALUE) + cur.distance(neighbor);
                double neiDist = disbetwn.getOrDefault(neighbor, Double.MAX_VALUE);
                if (newDist < neiDist) {
                    pathtrvld.put(neighbor, cur);
                    disbetwn.put(neighbor, newDist);
                    needexploring.add(neighbor);
                }
        }
    }
    fin.add(end);
        Point curr = end;
        boolean ispath = true;
        while (true){
            Point temp = pathtrvld.get(curr);
            if (temp == null){
                ispath = false; break;
            }
            if (temp.equals(start)){
                fin.add(temp); break;
            }
            curr = temp;
            fin.add(curr);

        }
        // path exception
        if (!ispath){
            throw new InvalidAlgorithmParameterException("No path between start and end");
        }
        if (ispath) {
            Collections.reverse(fin);
            return fin;
        }
        return null;
    }


    
}












