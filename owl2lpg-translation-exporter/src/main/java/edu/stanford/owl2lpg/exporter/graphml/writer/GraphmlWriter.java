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

  @Inject
  public GraphmlWriter(@Nonnull TinkerGraph graph,
                       @Nonnull Path output) {
    this.output = checkNotNull(output);
    this.graphWriter = graph;
    this.g = traversal().withEmbedded(this.graphWriter);
    this.nodeCache = new HashMap<>();
  }

  /**
   * If a node has already been added the get that.
   * Set the label and properties.
   *
   * @param node
   * @return
   */
  private Vertex acquireNode(Node node) {
    var nodeId = node.getNodeId();
    if (this.nodeCache.containsKey(nodeId)) {
      return this.nodeCache.get(nodeId);
    }
    var nodeLabels = node.getLabels();
    var nodeMainLabel = nodeLabels.getMainLabel();

    var tr = this.g.addV(nodeMainLabel);
    for (var entry : node.getProperties().getMap().entrySet()) {
      tr = tr.property(entry.getKey(), entry.getValue());
    }
    final Vertex vertex = tr.next();
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
    acquireNode(node);
  }

  /**
   * @param edge
   * @throws IOException
   */
  public void writeEdge(@Nonnull Edge edge) throws IOException {
    var label = edge.getLabel();
    var tr = g.addE(label.getName())
            .to(acquireNode(edge.getToNode()))
            .from(acquireNode(edge.getFromNode()));
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
