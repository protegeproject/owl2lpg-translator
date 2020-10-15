package edu.stanford.owl2lpg.translator.shared;

import dagger.Binds;
import dagger.Module;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class OntologyObjectSerializerModule {

  @Binds
  public abstract OntologyObjectSerializer
  provideOntologyObjectSerializer(BinaryOntologyObjectSerializer impl);
}
