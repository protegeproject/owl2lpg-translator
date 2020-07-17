package edu.stanford.owl2lpg.client.read.index;

import dagger.Component;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;
import edu.stanford.owl2lpg.client.read.axiom.AxiomSubjectAccessorModule;
import edu.stanford.owl2lpg.client.read.axiom.handlers.OwlDataFactoryModule;
import edu.stanford.owl2lpg.client.read.frame2.FrameAxiomsContextModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    FrameAxiomsContextModule.class,
    AxiomSubjectAccessorModule.class,
    OwlDataFactoryModule.class
})
@DatabaseSessionScope
public interface FrameAxiomsIndexComponent {

  Neo4jClassFrameAxiomsIndex getClassFrameAxiomsIndex();

  Neo4jNamedIndividualFrameAxiomsIndex getNamedIndividualFrameAxiomsIndex();
}
