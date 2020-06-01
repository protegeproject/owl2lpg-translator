package edu.stanford.owl2lpg.client.read.frame;

import dagger.Component;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = FrameAccessorModule.class)
@DatabaseSessionScope
public interface FrameAccessorComponent {

  ClassFrameAccessor getClassFrameAccessor();

  ObjectPropertyFrameAccessor getObjectPropertyFrameAccessor();

  DataPropertyFrameAccessor getDataPropertyFrameAccessor();

  AnnotationPropertyFrameAccessor getAnnotationPropertyFrameAccessor();

  NamedIndividualFrameAccessor getNamedIndividualFrameAccessor();
}
