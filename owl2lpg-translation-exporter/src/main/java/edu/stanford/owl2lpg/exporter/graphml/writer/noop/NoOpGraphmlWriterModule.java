package edu.stanford.owl2lpg.exporter.graphml.writer.noop;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.exporter.common.writer.NodeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.noop.NoOpEdgeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.noop.NoOpNodeTracker;
import edu.stanford.owl2lpg.exporter.common.writer.noop.NoOpWriter;
import edu.stanford.owl2lpg.exporter.common.writer.EdgeTracker;
import edu.stanford.owl2lpg.exporter.graphml.wip.GraphmlMapper;
import edu.stanford.owl2lpg.exporter.graphml.writer.*;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class NoOpGraphmlWriterModule {

  @Provides
  @TranslationSessionScope
  public GraphmlWriter<Node> provideNodeGraphmlWriter() {
    return new NoOpGraphmlWriter<Node>(
        new GraphmlMapper(),
        new Neo4jNodeGraphmlSchema(),
        new NoOpWriter());
  }

  @Provides
  @TranslationSessionScope
  public GraphmlWriter<Edge> provideEdgeGraphmlWriter() {
    return new NoOpGraphmlWriter<Edge>(
        new GraphmlMapper(),
        new Neo4jRelationshipsGraphmlSchema(),
        new NoOpWriter());
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
