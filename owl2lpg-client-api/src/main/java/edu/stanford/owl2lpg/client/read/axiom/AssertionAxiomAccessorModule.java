package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.NodeMapperModule;
import edu.stanford.owl2lpg.client.read.axiom.impl.AssertionAxiomAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    NodeMapperModule.class,
    ClassAssertionAxiomAccessorModule.class,
    AnnotationAssertionAxiomAccessorModule.class})
public abstract class AssertionAxiomAccessorModule {

  @Binds
  public abstract AssertionAxiomAccessor
  provideAssertionAxiomBySubjectAccessor(AssertionAxiomAccessorImpl impl);
}
