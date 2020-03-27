package edu.stanford.owl2lpg.datastructure;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Properties;

/**
 * A factory code for building labelled property graph.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class GraphFactory {

  public static Graph Graph(Edge... edges) {
    return new Graph(Lists.newArrayList(edges));
  }

  public static Graph Graph(List<Edge> edges) {
    return new Graph(edges);
  }

  public static Node Node(List<String> labels, Properties properties) {
    return new Node(labels, properties);
  }

  public static Node Node(List<String> labels) {
    return new Node(labels, new Properties());
  }

  public static Edge Edge(Node fromNode, AnyNode toNode, String label, Properties properties) {
    return new Edge(fromNode, toNode, label, properties);
  }

  public static Edge Edge(Node fromNode, AnyNode toNode, String label) {
    return new Edge(fromNode, toNode, label, new Properties());
  }
}
