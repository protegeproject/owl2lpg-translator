package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.Sets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SetBasedCsvExportChecker implements CsvExportChecker {

  private static final int DEFAULT_INITIAL_CAPACITY = 1_000_000;

  @Nonnull
  private final Set<String> exportedNodes;

  @Nonnull
  private final Set<EdgeKey> exportedEdges;

  public SetBasedCsvExportChecker() {
    this(DEFAULT_INITIAL_CAPACITY);
  }

  public SetBasedCsvExportChecker(int initialCapacity) {
    exportedNodes = Sets.newHashSetWithExpectedSize(initialCapacity);
    exportedEdges = Sets.newHashSetWithExpectedSize(initialCapacity);
  }

  @Override
  public boolean isExported(Node node) {
    return !exportedNodes.add(node.getNodeId().getId());
  }

  @Override
  public boolean isExported(Edge edge) {
    var edgeKey = EdgeKey.create(edge.getStartId(), edge.getEndId(), edge.getLabel());
    return !exportedEdges.add(edgeKey);
  }
}
