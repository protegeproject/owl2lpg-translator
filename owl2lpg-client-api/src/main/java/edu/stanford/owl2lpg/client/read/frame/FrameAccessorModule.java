package edu.stanford.owl2lpg.client.read.frame;

import dagger.Binds;
import dagger.Module;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    PlainFrameAccessorModule.class,
    ComparatorModule.class
})
public abstract class FrameAccessorModule {

  @Binds
  public abstract IriShortFormsAccessor
  provideIriShortFormsAccessor(IriShortFormsAccessorImpl accessor);

  @Binds
  public abstract ClassFrameAccessor
  provideClassFrameAccessor(ClassFrameAccessorImpl accessor);

  @Binds
  public abstract ObjectPropertyFrameAccessor
  provideObjectPropertyFrameAccessor(ObjectPropertyFrameAccessorImpl accessor);

  @Binds
  public abstract DataPropertyFrameAccessor
  provideDataPropertyFrameAccessor(DataPropertyFrameAccessorImpl accessor);

  @Binds
  public abstract AnnotationPropertyFrameAccessor
  provideAnnotationPropertyFrameAccessor(AnnotationPropertyFrameAccessorImpl accessor);

  @Binds
  public abstract NamedIndividualFrameAccessor
  provideNamedIndividualFrameAccessor(NamedIndividualFrameAccessorImpl accessor);
}
