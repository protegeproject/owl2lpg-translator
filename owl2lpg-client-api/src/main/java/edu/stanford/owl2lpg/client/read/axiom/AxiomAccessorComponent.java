package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Component;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;
import edu.stanford.owl2lpg.client.read.axiom.handlers.OwlDataFactoryModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    AxiomSubjectAccessorModule.class,
    HierarchyAxiomBySubjectAccessorModule.class,
    AssertionAxiomBySubjectAccessorModule.class,
    OwlDataFactoryModule.class
})
@DatabaseSessionScope
public interface AxiomAccessorComponent {

  AxiomSubjectAccessor getAxiomSubjectAccessor();

  HierarchyAxiomBySubjectAccessor getHierarchyAxiomBySubjectAccessor();

  AssertionAxiomBySubjectAccessor getAssertionAxiomBySubjectAccessor();
}
