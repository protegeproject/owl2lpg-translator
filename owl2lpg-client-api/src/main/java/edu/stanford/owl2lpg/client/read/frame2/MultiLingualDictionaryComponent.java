package edu.stanford.owl2lpg.client.read.frame2;

import dagger.Component;
import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    DatabaseModule.class,
    MultiLingualDictionaryContextModule.class,
    CypherMultiLingualShortFormModule.class
})
@DatabaseSessionScope
public interface MultiLingualDictionaryComponent {

  MultiLingualDictionaryImpl getMultiLingualDictionary();
}
