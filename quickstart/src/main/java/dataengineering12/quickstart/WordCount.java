/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataengineering12.quickstart;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.*;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Vertex;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.apache.flink.graph.Graph;
import org.apache.flink.types.NullValue;

/**
 *
 * @author Jos
 */
public class WordCount {
    private static List<Edge<String, Double>> edges = new ArrayList();
    private static List<Vertex<String, Long>> vertices= new ArrayList();    

    public static void input() throws IOException {
        String csvFile = "C:/Users/Jos/Documents/Data Engineering/edges.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                //System.out.println("Line to read: " + line);
                String[] toProcess = line.split(cvsSplitBy);
                processLine(toProcess);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Reading from CSV");
        input();
        System.out.println("CSV is in memory");
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        System.out.println("Number of Edges in Array: "+ edges.size());
        DataSet<Edge<String, Double>> dEdges = env.fromCollection(edges);
        //DataSet<Vertex<String, Long>> dVertices = env.fromCollection(vertices);
        
        Graph<String, NullValue, Double> graph;
        System.out.println("Loading Graph from Memory");
        graph = Graph.fromDataSet(dEdges, env);
        System.out.println("Computing number of edges");
        //System.out.println("Number of Edges: " + graph.numberOfEdges());
        System.out.println("Number of Vertices: "+ graph.getVertices().count());
        System.out.println("INGELADEN");
    }

    public static void processLine(String[] s){
        if(s.length > 1){
            edges.add(new Edge(s[0], s[1], 0.0));
        }
    }
    
}
