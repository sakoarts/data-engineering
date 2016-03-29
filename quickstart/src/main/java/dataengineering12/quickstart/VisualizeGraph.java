/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataengineering12.quickstart;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Vertex;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;

/**
 *
 * @author Sako Arts
 */
public class VisualizeGraph {

    final private static Graph graph = new SingleGraph("DBLP Graph");
    final private List<Edge<String, Double>> edges;
    final private List<Vertex<String, Long>> vertices;
    final private HashMap<Long, String> colors = new HashMap<>();
    private String author;

    public VisualizeGraph(List<Vertex<String, Long>> V, List<Edge<String, Double>> E) {
        edges = E;
        vertices = V;
        this.buildGraph();
    }

    private void buildGraph() {
        vertices.stream().forEach((tuple) -> {
            try {
                String style = getColor(tuple.f1);
                graph.addNode(tuple.f0).addAttribute("ui.style", style);
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        edges.stream().forEach((tuple) -> {
            try {
                
                graph.addEdge((tuple.f0 + tuple.f1), tuple.f0, tuple.f1);
            } catch (Exception e) {
            }
        });
    }

    private String getColor(Long l) {
        if (colors.containsKey(l)) {
            return colors.get(l);
        } else {
            Random rand = new Random();
            int r = (int) (rand.nextInt(256) / 2 + 127);
            int g = (int) (rand.nextInt(256) / 2 + 127);
            int b = (int) (rand.nextInt(256) / 2 + 127);
            String color = "fill-color: rgb(" + r + "," + g + "," + b + ");";
            colors.put(l, color);
            return color;
        }
    }

    private void colorNeigh() {
        String style = "z-index: 10; size: 15px; fill-color: rgb(0,128,0);";
        vertices.stream().forEach((tuple) -> {
            if (tuple.f0.equals(author)) {
                vertices.stream().forEach((tuple2) -> {
                    if (Objects.equals(tuple.f1, tuple2.f1)) {
                        graph.getNode(tuple2.f0).addAttribute("ui.style", style);
                    }
                });

            }
        });

    }

    public void setAuthor(String a) {
        author = a;
        colorNeigh();
        String style = "z-index: 20; size: 30px; fill-color: green;";
        try{
        graph.getNode(author).addAttribute("ui.style", style);
        } catch (Exception e) {
            
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public void displayGraph() {
        graph.display();
    }

    public void extractImage(int i) throws IOException {
        FileSinkImages pic = new FileSinkImages(FileSinkImages.OutputType.PNG, FileSinkImages.Resolutions.HD1080);

        pic.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);

        pic.writeAll(graph, "images/vdAalst_" + i + ".png");
    }
}
