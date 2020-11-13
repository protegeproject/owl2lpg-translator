package edu.stanford.owl2lpg.exporter.csv.writer;

import dagger.Binds;
import dagger.Module;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class WriteOpTrackerModule {

  @Binds
  public abstract NodeTracker provideNodeTracker(HashSetNodeTracker impl);

  @Binds
  public abstract EdgeTracker provideEdgeTracker(HashSetEdgeTracker impl);
}
