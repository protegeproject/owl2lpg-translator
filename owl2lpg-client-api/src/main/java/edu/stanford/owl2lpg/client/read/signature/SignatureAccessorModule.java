package edu.stanford.owl2lpg.client.read.signature;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.signature.impl.OntologySignatureAccessorImpl;
import edu.stanford.owl2lpg.client.read.signature.impl.ProjectSignatureAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class SignatureAccessorModule {

  @Binds
  public abstract OntologySignatureAccessor
  provideOntologySignatureAccessor(OntologySignatureAccessorImpl impl);

  @Binds
  public abstract ProjectSignatureAccessor
  provideProjectSignatureAccessor(ProjectSignatureAccessorImpl impl);
}
