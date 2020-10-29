package edu.stanford.owl2lpg.client.read.ontology;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.ontology.impl.OntologyDocumentAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class OntologyDocumentAccessorModule {

  @Binds
  public abstract OntologyDocumentAccessor provideOntologyAccessor(OntologyDocumentAccessorImpl impl);
}
