package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Component;
import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;
import edu.stanford.owl2lpg.client.read.handlers.OwlDataFactoryModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    DatabaseModule.class,
    OwlDataFactoryModule.class,
    AxiomAccessorModule.class,
    AxiomBySubjectAccessorModule.class,
    HierarchyAxiomBySubjectAccessorModule.class,
    AssertionAxiomBySubjectAccessorModule.class,
    AnnotationAssertionAxiomAccessorModule.class})
@DatabaseSessionScope
public interface AxiomAccessorComponent {

  AxiomAccessor getAxiomAccessor();

  AxiomBySubjectAccessor getAxiomBySubjectAccessor();

  HierarchyAxiomBySubjectAccessor getHierarchyAxiomBySubjectAccessor();

  AssertionAxiomBySubjectAccessor getAssertionAxiomBySubjectAccessor();
}
