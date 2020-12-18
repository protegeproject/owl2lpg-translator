package edu.stanford.owl2lpg.exporter.graphml.writer.noop;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.exporter.common.writer.EdgeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.NodeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.noop.NoOpEdgeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.noop.NoOpNodeTracker;
import edu.stanford.owl2lpg.exporter.graphml.writer.GraphmlWriter;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.nio.file.Paths;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class NoOpGraphmlWriterModule {

  @Provides
  @TranslationSessionScope
  public GraphmlWriter provideGraphmlWriter() {
    return new NoOpGraphmlWriter(
            TinkerGraph.open(),
            Paths.get("").toAbsolutePath().normalize().resolve("output.graphml") );
  }

  @Provides
  @TranslationSessionScope
  public NodeTracker provideNodeTracker() {
    return new NoOpNodeTracker();
  }

  @Provides
  @TranslationSessionScope
  public EdgeTracker provideEdgeTracker() {
    return new NoOpEdgeTracker();
  }
}
