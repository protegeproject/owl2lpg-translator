package edu.stanford.owl2lpg.client.bind.graph;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.write.ProjectUpdater;
import edu.stanford.owl2lpg.client.write.ProjectUpdaterImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class Neo4jGraphManagerModule {

  @Binds
  public abstract ProjectUpdater provideProjectUpdater(ProjectUpdaterImpl impl);
}
