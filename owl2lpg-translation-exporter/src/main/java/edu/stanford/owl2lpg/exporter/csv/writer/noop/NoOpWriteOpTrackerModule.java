package edu.stanford.owl2lpg.exporter.csv.writer.noop;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.exporter.csv.writer.EdgeTracker;
import edu.stanford.owl2lpg.exporter.csv.writer.NodeTracker;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class NoOpWriteOpTrackerModule {

  @Binds
  public abstract NodeTracker provideNodeTracker(NoOpNodeTracker impl);

  @Binds
  public abstract EdgeTracker provideEdgeTracker(NoOpEdgeTracker impl);
}
