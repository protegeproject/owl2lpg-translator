package edu.stanford.owl2lpg.translator.shared;

import dagger.Module;
import dagger.Provides;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    OntologyObjectSerializerModule.class,
    DigestFunctionModule.class})
public class OntologyObjectDigesterModule {

  @Provides
  public OntologyObjectDigester provideOntologyObjectDigester(OntologyObjectSerializer serializer,
                                                              BytesDigester bytesDigester) {
    return new OntologyObjectDigester(serializer, bytesDigester);
  }
}
