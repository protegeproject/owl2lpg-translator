package edu.stanford.owl2lpg.exporter.graphml.writer;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.exporter.common.writer.EdgeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.HashSetEdgeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.HashSetNodeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.NodeTracker;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;

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
              ? Paths.get("").toAbsolutePath().resolve("ontology.graphml").normalize()
              : outputPath;
  }

  @Provides
  @TranslationSessionScope
  public GraphmlWriter provideGraphmlWriter() {
    var conf = new BaseConfiguration();
//    conf.setProperty("gremlin.tinkergraph.defaultVertexPropertyCardinality","list");
    conf.setProperty("gremlin.tinkergraph.vertexIdManager","UUID");
    conf.setProperty("gremlin.tinkergraph.edgeIdManager","UUID");
    var graph = TinkerGraph.open(conf);
    return new GraphmlWriter(graph, outputPath);
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
