package edu.stanford.owl2lpg.client.read.frame2;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormIndex;
import edu.stanford.bmir.protege.web.server.shortform.SearchableMultiLingualShortFormDictionary;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class CypherMultiLingualShortFormModule {

  @Binds
  public abstract MultiLingualShortFormIndex
  provideMultiLingualShortFromIndex(CypherMultiLingualShortFormIndex impl);

  @Binds
  public abstract MultiLingualShortFormDictionary
  provideMultiLingualShortFormDictionary(CypherMultiLingualShortFormDictionary impl);

  @Binds
  public abstract SearchableMultiLingualShortFormDictionary
  provideSearchableMultiLingualShortFormDictionary(CypherSearchableMultiLingualShortFormDictionary impl);
}
