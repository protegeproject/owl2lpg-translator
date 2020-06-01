package edu.stanford.owl2lpg.client.read.frame;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.DatabaseModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    ObjectMapperModule.class,
    DatabaseModule.class})
abstract class PlainFrameAccessorModule {

  @Binds
  abstract PlainClassFrameAccessor
  providePlainClassFrameAccessor(PlainClassFrameAccessorImpl accessor);

  @Binds
  abstract PlainObjectPropertyFrameAccessor
  providePlainObjectPropertyFrameAccessor(PlainObjectPropertyFrameAccessorImpl accessor);

  @Binds
  abstract PlainDataPropertyFrameAccessor
  providePlainDataPropertyFrameAccessor(PlainDataPropertyFrameAccessorImpl accessor);

  @Binds
  abstract PlainAnnotationPropertyFrameAccessor
  providePlainAnnotationPropertyFrameAccessor(PlainAnnotationPropertyFrameAccessorImpl accessor);

  @Binds
  abstract PlainNamedIndividualFrameAccessor
  providePlainNamedIndividualFrameAccessor(PlainNamedIndividualFrameAccessorImpl accessor);
}
