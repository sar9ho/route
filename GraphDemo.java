/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 */
// public class GraphDemo {
//     public static void main(String[] args) {

//     }
// }







import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GraphDemo {
    public static void main(String[] args) throws Exception{
        GraphProcessor graphP = new GraphProcessor();
        Visualize demoVisualizer = new Visualize("data/usa.vis", "images/usa.png");
        FileInputStream input = new FileInputStream("data/usa.graph");
        graphP.initialize(input); // data initalized

        Scanner scanver = new Scanner(System.in); //user inputs cities here

        // SAN FRAN
        System.out.println("What is the Latitude of your orgin city?: ");
        Double lat1 = Double.valueOf(scanver.nextLine());     // 37.7558 
        System.out.println("What is the Longitude of your orgin city?: ");
        Double long1 = Double.valueOf(scanver.nextLine());   // -122.4449

        // BRONX NY
        System.out.println("What is the Latitude of your destination?: ");
        Double lat2 = Double.valueOf(scanver.nextLine());     // 40.8501
        System.out.println("What is the Longitude of your destination?: ");
        Double long2 = Double.valueOf(scanver.nextLine());   // -73.8662

        // coords for route from SanFran, CA to da Bronx, NY

        Point start = new Point(lat1, long1);
        Point end = new Point(lat2, long2);
        
        long findclosestart = System.nanoTime();
        Point firstP = graphP.nearestPoint(start);
        long proximalOrigin = System.nanoTime() - findclosestart;

        long findcloseend = System.nanoTime();
        Point lastP = graphP.nearestPoint(end);
        long proximalEnd = System.nanoTime() - findcloseend;

        if (graphP.connected(firstP, lastP)){

            long findRoute = System.nanoTime();
            List<Point> routes = graphP.route(firstP, lastP);
            long routeTime = System.nanoTime() - findRoute;
            
            long calcDist = System.nanoTime();
            double totalDist = graphP.routeDistance(routes);
            long distTime = System.nanoTime() - calcDist;

            long totalTime = TimeUnit.NANOSECONDS.toMillis(proximalOrigin + proximalEnd + routeTime + distTime);
            
            demoVisualizer.drawPoint(firstP);
            demoVisualizer.drawPoint(lastP);
            demoVisualizer.drawRoute(routes);

            System.out.println("This route took a total of " + totalDist + " miles.");
            System.out.println("The total time to get nearest points, route, and distance was" + totalTime + " ms.");
        }
        else{
            System.out.println("No route found, check coordinates.");
        }

        
    }
}







// -------------------



// import java.security.InvalidAlgorithmParameterException;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.util.*;

// /**
//  * Models a weighted graph of latitude-longitude points
//  * and supports various distance and routing operations.
//  * To do: Add your name(s) as additional authors
//  * @author Brandon Fain
//  * @author Lex Schier
//  *
//  */
// public class GraphProcessor {
    

//     private Map <Point, Integer> connectedMap;
//     private List<Point> info;
//     private Map <Point, List<Point>> adjacent;
//     private int numVert;
//     private int numEdges;
    

//     /**
//      * Creates and initializes a graph from a source data
//      * file in the .graph format. Should be called
//      * before any other methods work.
//      * @param file a FileInputStream of the .graph file
//      * @throws FileNotFoundException
//      * @throws Exception if file not found or error reading
//      */
    
//     public void initialize(FileInputStream file) throws FileNotFoundException {
        
//         Scanner reader = new Scanner(file);
        
//         //if the file isn't a .graph file, throw this error
//         if (!reader.hasNextInt()){
//             reader.close();
//             throw new FileNotFoundException("Could not read .graph file");
//         }

//         String[] vertedge = reader.nextLine().split(" ");

//         numVert = Integer.parseInt(vertedge[0]);
//         numEdges = Integer.parseInt(vertedge[1]);

//         info = new ArrayList<>();

//         for (int i = 0; i < numVert; i++){
//             String[] temp = reader.nextLine().split(" ");
//             info.add(new Point(Double.parseDouble(temp[1]),Double.parseDouble(temp[2])));
//         }

        

//         //System.out.println(info.entrySet());

//         adjacent = new HashMap<>();

//         for (int i = 0; i < numEdges; i++){
//             String[] temp = reader.nextLine().split(" ");
//             Point a = info.get(Integer.parseInt(temp[0]));
//             Point b = info.get(Integer.parseInt(temp[1]));
//             if (!adjacent.containsKey(a)){
//                 adjacent.put(a, new ArrayList<>());
//             }
//             if (!adjacent.containsKey(b)){
//                 adjacent.put(b, new ArrayList<>());
//             }
//             adjacent.get(a).add(b);
//             adjacent.get(b).add(a);
//         }

//         fillConnections();

//         //System.out.println(adjacent.entrySet());

//         reader.close();
//     }

//     private void fillConnections(){
//         connectedMap = new HashMap<>();
//         Set<Point> visited = new HashSet<>();
//         int component = 0;
//         for (Point p1: info){
//             if (visited.contains(p1)){
//                 continue;
//             }

//             Stack<Point> toExplore = new Stack<>();
//             Point current;
//             toExplore.add(p1);
//             visited.add(p1);
//             connectedMap.put(p1, component);
//             while (!toExplore.isEmpty()){
//                 current = toExplore.pop();
//                 for (Point neighbor : adjacent.get(current)){
//                     if (!visited.contains(neighbor)){
//                         visited.add(neighbor);
//                         connectedMap.put(neighbor, component);
//                         toExplore.push(neighbor);
//                     }
//                 }
//             }
//             component += 1;
//         }
//     }

//     /**
//      * Searches for the point in the graph that is closest in
//      * straight-line distance to the parameter point p
//      * @param p A point, not necessarily in the graph
//      * @return The closest point in the graph to p
//      */
//     public Point nearestPoint(Point p) {
//         double nearDist = 100000000;
//         Point nearPoint = info.get(0);
        
//         for (Point ele : info){
//             if (nearDist > p.distance(ele)){
//                 nearDist = p.distance(ele);
//                 nearPoint = ele;
//             }
//         }

//         return nearPoint;
//     }


//     /**
//      * Calculates the total distance along the route, summing
//      * the distance between the first and the second Points, 
//      * the second and the third, ..., the second to last and
//      * the last. Distance returned in miles.
//      * @param start Beginning point. May or may not be in the graph.
//      * @param end Destination point May or may not be in the graph.
//      * @return The distance to get from start to end
//      */
//     public double routeDistance(List<Point> route) {
//         double sum = 0.0;
        
//         for (int i = 0; i < route.size()-1; i++){
//             sum += route.get(i).distance(route.get(i+1));
//         }

//         /* 
//         if (!info.containsValue(route.get(0))){ //if the first Point is not in the info list
//             Point temp = nearestPoint(route.get(0));
//             sum += route.get(0).distance(temp);
//             start+=1;
//         }

//         for (int i = start; i < route.size()-1; i++){
//             sum +=route.get(i-1).distance(route.get(i));
//         }

//         Point last = route.get(lengthRt-1);

//         if (!info.containsValue(last)){
//             Point temp = nearestPoint(last);
//             sum+= route.get(lengthRt-2).distance(temp);
//             sum += last.distance(temp);
            
//         }
//         else{
//             sum += last.distance(route.get(lengthRt-2));
//         }

//         */

//         return sum;
//     }
    
//     /**
//      * Checks if input points are part of a connected component
//      * in the graph, that is, can one get from one to the other
//      * only traversing edges in the graph
//      * @param p1 one point
//      * @param p2 another point
//      * @return true if p2 is reachable from p1 (and vice versa)
//      */
//     public boolean connected(Point p1, Point p2) {
//         if (connectedMap.containsKey(p1) && connectedMap.containsKey(p2)){
//             if (connectedMap.get(p1) == connectedMap.get(p2)){
//                 return true;
//             }
//         }
//         return false;
//     }

//     /**
//      * Returns the shortest path, traversing the graph, that begins at start
//      * and terminates at end, including start and end as the first and last
//      * points in the returned list. If there is no such route, either because
//      * start is not connected to end or because start equals end, throws an
//      * exception.
//      * @param start Beginning point.
//      * @param end Destination point.
//      * @return The shortest path [start, ..., end].
//      * @throws InvalidAlgorithmParameterException if there is no such route, 
//      * either because start is not connected to end or because start equals end.
//      */
//     public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
//         List<Point> ret = new ArrayList<>();

//         HashSet<Point> visited = new HashSet<>();
//         HashMap<Point, Double> distanceBetween = new HashMap<>();
//         HashMap<Point, Point> travelledPath = new HashMap<>();
        
//         Comparator<Point> comp = (a, b) -> {double first = distanceBetween.get(a); 
//                                             double second = distanceBetween.get(b); 
//                                             return Double.compare(first, second);
//         };
//         PriorityQueue<Point> toExplore = new PriorityQueue<>(comp);

//         if (!adjacent.containsKey(end) || !adjacent.containsKey(start)){
//             throw new InvalidAlgorithmParameterException("No path between start and end");
//         }

//         distanceBetween.put(start, 0.0);
//         toExplore.add(start);
        
//         while (!toExplore.isEmpty()) {
//             Point current = toExplore.remove();
//             if (current.equals(end)){
//                 break;
//             }
//             visited.add(current);
//             List<Point> vals = adjacent.get(current);
//             for (Point neighbor : vals) {
                
//                 if (visited.contains(neighbor)) continue;
//                 double newDist = distanceBetween.getOrDefault(current,Double.MAX_VALUE) + current.distance(neighbor);
//                 double neiDist = distanceBetween.getOrDefault(neighbor, Double.MAX_VALUE);
//                 if (newDist < neiDist) {
//                     travelledPath.put(neighbor,current);
//                     distanceBetween.put(neighbor, newDist);
//                     toExplore.add(neighbor);
//                 }
//             }
//         }

//         ret.add(end);
//         Point current = end;
//         boolean ispath = true;
//         while (true){
//             Point temp = travelledPath.get(current);
//             if (temp == null){
//                 ispath = false; break;
//             }
//             if (temp.equals(start)){
//                 ret.add(temp); break;
//             }
//             current = temp;
//             ret.add(current);

//         }

//         if (!ispath){
//             throw new InvalidAlgorithmParameterException("No path between start and end");
//         }
//         if (ispath) {
//             Collections.reverse(ret);
//             return ret;
//         }
//         return null;
//     }

    
// }
