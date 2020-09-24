package edu.stanford.owl2lpg.translator.shared;

import dagger.Module;
import dagger.Provides;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class BuiltInPrefixDeclarationsModule {

  @Provides
  public BuiltInPrefixDeclarations provideBuiltInPrefixDeclarations() {
    var prefixMap = PrefixDeclarationsCsvParser.getBuiltInPrefixes("built-in-prefixes.csv");
    return BuiltInPrefixDeclarations.get(prefixMap);
  }
}
