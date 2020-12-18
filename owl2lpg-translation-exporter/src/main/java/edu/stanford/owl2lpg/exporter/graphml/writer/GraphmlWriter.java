package edu.stanford.owl2lpg.exporter.graphml.writer;

import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

public class GraphmlWriter implements AutoCloseable {
  static Logger log = LoggerFactory.getLogger(GraphmlWriter.class);

  @Nonnull
  private final Path output;

  @Nonnull
  private TinkerGraph graphWriter;

  private final GraphTraversalSource g;
  private final Map<NodeId, Vertex> nodeCache;
  private final Map<NodeId, Edge> edgePending;

  private final static String UNLABELLED = "UNLABELLED";

  @Inject
  public GraphmlWriter(@Nonnull TinkerGraph graph,
                       @Nonnull Path output) {
    this.output = checkNotNull(output);
    this.graphWriter = graph;
    this.g = traversal().withEmbedded(this.graphWriter);
    this.nodeCache = new HashMap<>();
    this.edgePending = new HashMap<>();
  }

  /**
   * If you want to swap one vertex for another preserving all of its
   * edges and properties.
   * @param oldV
   * @param newV
   * @return
   */
  private Vertex replaceVertex(Vertex oldV, Vertex newV) {
    this.g  .V(newV).as("new")
            .V(oldV).as("old").sideEffect(__.drop())
            .inE().as("oldIn").sideEffect(__.drop())
            .outV().as("oldFrom")
            .addE(__.select("oldIn").label()).from("oldFrom").to("new")
            .select("old")
            .outE().as("oldOut").sideEffect(__.drop())
            .inV().as("oldTo")
            .addE(__.select("oldOut").label()).from("new").to("oldTo")
            .iterate();
    return newV;
  }
  /**
   * If a node has already been added the get that.
   * If a label is provided then set that.
   *
   * @param nodeId
   * @param label
   * @return
   */
  private Vertex acquireNode(NodeId nodeId, String label) {
    if (this.nodeCache.containsKey(nodeId)) {
      var vertex1 = this.nodeCache.get(nodeId);
      if (vertex1.label() != UNLABELLED) {
        return vertex1;
      }
      log.info("node not yet registered {}", label);
      final Vertex vertex2 = replaceVertex(vertex1, this.g.addV(label).next());
      this.nodeCache.put(nodeId, vertex2);
      return vertex2;
    }
    final Vertex vertex = this.g.addV(label).next();
    this.nodeCache.put(nodeId, vertex);
    return vertex;
  }

  /**
   * Write the Node information into the graph.
   * Use the gramphml-schema as a guide.
   *
   * @param node
   * @throws IOException
   */
  public void writeNode(@Nonnull Node node) throws IOException {
    var labels = node.getLabels();
    var tr = g.V(acquireNode(node.getNodeId(), labels.getMainLabel()));
    for (var entry : node.getProperties().getMap().entrySet()) {
      tr = tr.property(entry.getKey(), entry.getValue());
    }
    tr.iterate();
  }

  public void writeEdge(@Nonnull Edge edge) throws IOException {
    var label = edge.getLabel();
    var tr = g.addE(label.getName())
            .to(acquireNode(edge.getToNode().getNodeId(), UNLABELLED))
            .from(acquireNode(edge.getFromNode().getNodeId(), UNLABELLED));
    for (var entry : edge.getProperties().getMap().entrySet()) {
      tr = tr.property(entry.getKey(), entry.getValue());
    }
    tr.iterate();
  }

  /**
   * maybe complete the transaction
   * @throws IOException
   */
  public void flush() throws IOException {
    log.info("flush", "foo");
  }

  @Override
  public void close() throws IOException {
    var size = g.V().count().next();
    log.info("number of vertices {}", size);
    g.io(this.output.toAbsolutePath().toString())
            .with(IO.writer, IO.graphml)
            .write()
            .iterate();
  }
}