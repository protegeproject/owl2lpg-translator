package edu.stanford.owl2lpg.client.bind.index;

import dagger.Module;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessorModule;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessorModule;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessorModule;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessorModule;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessorModule;
import edu.stanford.owl2lpg.client.read.individual.NamedIndividualAccessorModule;
import edu.stanford.owl2lpg.client.read.ontology.OntologyDocumentAccessorModule;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessorModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    ProjectAccessorModule.class,
    OntologyDocumentAccessorModule.class,
    OntologyAnnotationsAccessorModule.class,
    EntityAccessorModule.class,
    NamedIndividualAccessorModule.class,
    AxiomAccessorModule.class,
    AssertionAxiomAccessorModule.class,
    CharacteristicsAxiomAccessorModule.class})
public abstract class Neo4jOntologyObjectAccessorModule {
}
