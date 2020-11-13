package edu.stanford.owl2lpg.translator.shared;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import dagger.Module;
import dagger.Provides;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@SuppressWarnings("UnstableApiUsage")
@Module
public class HashFunctionModule {

  @Provides
  public HashFunction provideHashFunction() {
    return Hashing.sha256();
  }
}
