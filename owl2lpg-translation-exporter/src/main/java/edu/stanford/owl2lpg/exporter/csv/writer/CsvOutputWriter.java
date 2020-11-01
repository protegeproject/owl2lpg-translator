package edu.stanford.owl2lpg.exporter.csv.writer;

import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Translation;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CsvOutputWriter {

  @Nonnull
  private final Set<CsvWriter<Node>> nodeCsvWriters;

  @Nonnull
  private final Set<CsvWriter<Edge>> edgeCsvWriters;

  @Nonnull
  private final NodeTracker nodeTracker;

  @Nonnull
  private final EdgeTracker edgeTracker;

  @Nonnull
  private final ProgressTracker progressTracker;

  @Inject
  public CsvOutputWriter(@Nonnull Set<CsvWriter<Node>> nodeCsvWriters,
                         @Nonnull Set<CsvWriter<Edge>> edgeCsvWriters,
                         @Nonnull NodeTracker nodeTracker,
                         @Nonnull EdgeTracker edgeTracker,
                         @Nonnull ProgressTracker progressTracker) {
    this.nodeCsvWriters = checkNotNull(nodeCsvWriters);
    this.edgeCsvWriters = checkNotNull(edgeCsvWriters);
    this.nodeTracker = checkNotNull(nodeTracker);
    this.edgeTracker = checkNotNull(edgeTracker);
    this.progressTracker = checkNotNull(progressTracker);
  }

  public void write(@Nonnull Translation translation) {
    var node = translation.getMainNode();
    write(node);
    for (var edge : translation.getEdges()) {
      write(edge);
    }
    for (var nestedTranslation : translation.getNestedTranslations()) {
      write(nestedTranslation);
    }
  }

  public void write(@Nonnull Node node) {
    nodeTracker.add(node, this::writeNode);
  }

  private void writeNode(Node node) {
    nodeCsvWriters.stream()
        .filter(writer -> writer.isCompatible(node))
        .findFirst()
        .ifPresent(writer -> {
          try {
            writer.write(node);
            writer.flush();
            progressTracker.increaseCount(node);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  public void write(@Nonnull Edge edge) {
    edgeTracker.add(edge, this::writeEdge);
  }

  private void writeEdge(Edge edge) {
    edgeCsvWriters.stream()
        .filter(writer -> writer.isCompatible(edge))
        .findFirst()
        .ifPresent(writer -> {
          try {
            writer.write(edge);
            writer.flush();
            progressTracker.increaseCount(edge);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  public long getTrackedNodeCount() {
    return nodeTracker.size();
  }

  public long getTrackedEdgeCount() {
    return edgeTracker.size();
  }

  public long getExportedNodeCount() {
    return progressTracker.getNodeCount();
  }

  public long getExportedEdgeCount() {
    return progressTracker.getEdgeCount();
  }

  public void printReport() {
    progressTracker.printReport();
  }
}
