package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.axiom.impl.DomainAxiomAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class DomainAxiomAccessorModule {

  @Binds
  public abstract DomainAxiomAccessor provideDomainAxiomAccessor(DomainAxiomAccessorImpl impl);
}
