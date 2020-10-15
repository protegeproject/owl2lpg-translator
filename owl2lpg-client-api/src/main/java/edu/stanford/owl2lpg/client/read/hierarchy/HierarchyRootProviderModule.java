package edu.stanford.owl2lpg.client.read.hierarchy;

import dagger.Module;
import dagger.Provides;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyRoot;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyRootProvider;
import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyRoot;
import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyRootProvider;
import edu.stanford.bmir.protege.web.server.hierarchy.ObjectPropertyHierarchyRoot;
import edu.stanford.bmir.protege.web.server.hierarchy.ObjectPropertyHierarchyRootProvider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class HierarchyRootProviderModule {

  @Provides
  @ClassHierarchyRoot
  public OWLClass provideClassHierarchyRoot(ClassHierarchyRootProvider provider) {
    return provider.get();
  }

  @Provides
  @ObjectPropertyHierarchyRoot
  public OWLObjectProperty provideObjectPropertyHierarchyRoot(ObjectPropertyHierarchyRootProvider provider) {
    return provider.get();
  }

  @Provides
  @DataPropertyHierarchyRoot
  public OWLDataProperty provideDataPropertyHierarchyRoot(DataPropertyHierarchyRootProvider provider) {
    return provider.get();
  }
}
