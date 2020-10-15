package edu.stanford.owl2lpg.client.read.annotation;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.annotation.impl.OntologyAnnotationsAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class OntologyAnnotationsAccessorModule {

  @Binds
  public abstract OntologyAnnotationsAccessor
  provideOntologyAnnotationsAccessor(OntologyAnnotationsAccessorImpl impl);
}
