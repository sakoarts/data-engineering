/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataengineering12.quickstart;

import java.util.List;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Vertex;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author Sako Arts
 */
public class VisualizeGraph {

    final private static Graph graph = new SingleGraph("DBLP Graph");
    final private List<Edge<String, Double>> edges;
    final private List<Vertex<String, Long>> vertices;

    public VisualizeGraph(List<Vertex<String, Long>> V, List<Edge<String, Double>> E) {
        edges = E;
        vertices = V;
        this.buildGraph();
    }
    
    private void buildGraph() {
        vertices.stream().forEach((tuple) -> {
            try {
                graph.addNode(tuple.f0);
            } catch (Exception e) {
            }
        });
        edges.stream().forEach((tuple) -> {
            try {
                graph.addEdge((tuple.f0 + tuple.f1), tuple.f0, tuple.f1);
            } catch (Exception e) {
            }
        });
    }
    
    public Graph getGraph() {
        return graph;
    }

    public void displayGraph() {
        graph.display();
    }

}
