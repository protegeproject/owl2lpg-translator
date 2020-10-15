package edu.stanford.owl2lpg.client.read.shortform;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import edu.stanford.bmir.protege.web.server.jackson.ObjectMapperProvider;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ObjectMapperModule {

  @Provides
  @DatabaseSessionScope
  public ObjectMapper provideObjectMapper() {
    return new ObjectMapperProvider().get();
  }
}
