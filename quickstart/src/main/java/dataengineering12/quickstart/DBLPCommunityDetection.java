/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataengineering12.quickstart;

import org.apache.flink.api.java.*;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Vertex;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.library.CommunityDetection;
import org.apache.flink.types.NullValue;



/**
 *
 * @author Jos
 */
public class DBLPCommunityDetection {

    private static List<Edge<String, Double>> edges = new ArrayList();
    private static List<Vertex<String, Long>> vertices = new ArrayList();

    public static void input(int i) throws IOException {
        String csvFile = "C:/Dropbox/TUe/data-engineering/reduced.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            int c = 0;
            while ((line = br.readLine()) != null){ // && c <= 20000) {
                // use comma as separator
                //System.out.println("Line to read: " + line);
                String[] toProcess = line.split(cvsSplitBy);
                processLine(toProcess, i);
                c++;
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
        for (int i = 1970; i <= 2016; i++) {
            System.out.println("Reading from CSV");
            input(i);
            System.out.println("CSV is in memory");
            final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
            System.out.println("Number of Edges in Array: " + edges.size());
            if (edges.size() == 0) {
                continue;
            }
            DataSet<Edge<String, Double>> dEdges = env.fromCollection(edges);
            //DataSet<Vertex<String, Long>> dVertices = env.fromCollection(vertices);

            Graph<String, NullValue, Double> tgraph;
            System.out.println("Loading Graph from Memory");
            tgraph = Graph.fromDataSet(dEdges, env);
            List<String> dVertices = tgraph.getVertexIds().collect();
            DataSet<Vertex<String, Long>> finVertices = tgraph.getVertices().map(new MapFunction<Vertex<String, NullValue>, Vertex<String, Long>>() {
                @Override
                public Vertex<String, Long> map(Vertex<String, NullValue> t) {
                    return new Vertex(t.f0, Long.valueOf(dVertices.indexOf(t.f0)));
                }
            });
            Graph<String, Long, Double> graph;
            graph = Graph.fromDataSet(finVertices, dEdges, env);
            System.out.println("Number of Vertices: " + graph.getVertices().count());
            System.out.println("INGELADEN");
            Graph<String, Long, Double> graphWithCommunities;
            graphWithCommunities = graph.run(new CommunityDetection<String>(50, 0.5));

            List<Vertex<String, Long>> ve = graphWithCommunities.getVertices().collect();
            List<Edge<String, Double>> ed = graphWithCommunities.getEdges().collect();

            VisualizeGraph g = new VisualizeGraph(ve, ed);
            g.setAuthor("Wil M. P. van der Aalst");
            //g.displayGraph();
            g.extractImage(i);
        }
    }

    public static void processLine(String[] s, int i) {
        if (s.length > 1 && Integer.valueOf(s[2]) == i){// && (s[0].equals("Wil M. P. van der Aalst") || (s[1].equals("Wil M. P. van der Aalst")))) {
            edges.add(new Edge(s[0], s[1], 1.0));//Double.valueOf(s[2])));
        }
    }

    
}
