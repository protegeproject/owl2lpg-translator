package edu.stanford.owl2lpg.client.read.ontology;

import dagger.Component;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.read.NodeMapperModule;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessorModule;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessorModule;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessorModule;
import edu.stanford.owl2lpg.client.read.handlers.OwlDataFactoryModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    DatabaseModule.class,
    ProjectAccessorModule.class,
    OntologyDocumentAccessorModule.class,
    OntologyAnnotationsAccessorModule.class,
    AxiomAccessorModule.class,
    EntityAccessorModule.class,
    OwlDataFactoryModule.class,
    NodeMapperModule.class})
@ProjectSingleton
public interface ProjectAccessorComponent {

  ProjectAccessor getProjectAccessor();

  OntologyDocumentAccessor getOntologyDocumentAccessor();
}
