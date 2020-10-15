package edu.stanford.owl2lpg.client.read.ontology;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.ontology.impl.ProjectAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class ProjectAccessorModule {

  @Binds
  public abstract ProjectAccessor provideProjectAccessor(ProjectAccessorImpl impl);
}
