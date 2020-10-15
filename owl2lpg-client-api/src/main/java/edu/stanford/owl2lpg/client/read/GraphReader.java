package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.impl.NodeIndexImpl;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class GraphReader {

  @Nonnull
  private final Driver driver;

  @Inject
  public GraphReader(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  public boolean hasResult(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> tx.run(queryString, inputParams).list().isEmpty());
    }
  }

  @Nonnull
  public NodeIndex getNodeIndex(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var result = tx.run(queryString, inputParams);
        var nodeIndexBuilder = new NodeIndexImpl.Builder();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                path.spliterator().forEachRemaining(nodeIndexBuilder::add);
              }
            }
          }
        }
        return nodeIndexBuilder.build();
      });
    }
  }

  @Nonnull
  public ImmutableSet<Node> getNodes(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var nodes = ImmutableSet.<Node>builder();
        var result = tx.run(queryString, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("n")) {
              var node = (Node) column.getValue();
              if (node != null) {
                nodes.add(node);
              }
            }
          }
        }
        return nodes.build();
      });
    }
  }

  @Nonnull
  public ImmutableSet<Path> getPaths(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var paths = ImmutableSet.<Path>builder();
        var result = tx.run(queryString, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                paths.add(path);
              }
            }
          }
        }
        return paths.build();
      });
    }
  }
}
