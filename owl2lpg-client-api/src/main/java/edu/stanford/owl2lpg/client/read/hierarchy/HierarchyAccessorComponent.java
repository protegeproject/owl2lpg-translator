package edu.stanford.owl2lpg.client.read.hierarchy;

import dagger.Component;
import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;
import edu.stanford.owl2lpg.client.OntologyProjectModule;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessorModule;
import edu.stanford.owl2lpg.client.read.handlers.OwlDataFactoryModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    DatabaseModule.class,
    OntologyProjectModule.class,
    HierarchyAccessorModule.class,
    EntityAccessorModule.class,
    OwlDataFactoryModule.class})
@DatabaseSessionScope
public interface HierarchyAccessorComponent {

  ClassHierarchyAccessor getClassHierarchyAccessor();

  ObjectPropertyHierarchyAccessor getObjectPropertyHierarchyAccessor();

  DataPropertyHierarchyAccessor getDataPropertyHierarchyAccessor();

  AnnotationPropertyHierarchyAccessor getAnnotationPropertyHierarchyAccessor();
}
