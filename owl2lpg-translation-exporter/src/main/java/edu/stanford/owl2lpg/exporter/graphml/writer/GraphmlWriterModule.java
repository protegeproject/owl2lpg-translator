package edu.stanford.owl2lpg.exporter.graphml.writer;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.exporter.common.writer.HashSetEdgeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.HashSetNodeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.NodeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.EdgeTracker;
import edu.stanford.owl2lpg.exporter.graphml.wip.GraphmlMapper;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class GraphmlWriterModule {

  @Nonnull
  private final Path outputPath;

  public GraphmlWriterModule(@Nonnull Path outputPath) {
      this.outputPath = (outputPath == null)
              ? Paths.get("").toAbsolutePath().normalize()
              : outputPath;
  }

  @Provides
  @TranslationSessionScope
  public GraphmlWriter<Node> provideNodeGraphmlWriter() {
    try {
      var outputFile = new File(outputPath + File.separator + "graph.graphml");
      return new GraphmlWriter<Node>(
          new GraphmlMapper(),
          new Neo4jNodeGraphmlSchema(),
          new BufferedWriter(new FileWriter(outputFile)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides
  @TranslationSessionScope
  public GraphmlWriter<Edge> provideEdgeGraphmlWriter() {
    try {
      var outputFile = new File(outputPath + File.separator + "edges.csv");
      return new GraphmlWriter<Edge>(
          new GraphmlMapper(),
          new Neo4jRelationshipsGraphmlSchema(),
          new BufferedWriter(new FileWriter(outputFile)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides
  @TranslationSessionScope
  public NodeTracker provideNodeTracker() {
    return new HashSetNodeTracker();
  }

  @Provides
  @TranslationSessionScope
  public EdgeTracker provideEdgeTracker() {
    return new HashSetEdgeTracker();
  }
}
