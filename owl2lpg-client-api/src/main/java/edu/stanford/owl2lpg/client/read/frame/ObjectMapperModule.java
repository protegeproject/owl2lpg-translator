package edu.stanford.owl2lpg.client.read.frame;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import edu.stanford.bmir.protege.web.server.jackson.ObjectMapperProvider;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ObjectMapperModule {

  @Provides
  public ObjectMapper provideObjectMapper() {
    return new ObjectMapperProvider().get();
  }
}
