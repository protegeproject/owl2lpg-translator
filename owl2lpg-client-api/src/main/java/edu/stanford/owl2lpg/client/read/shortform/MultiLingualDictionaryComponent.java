package edu.stanford.owl2lpg.client.read.shortform;

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
    Neo4jMultiLingualShortFormModule.class
})
@DatabaseSessionScope
public interface MultiLingualDictionaryComponent {

  MultiLingualDictionaryImpl getMultiLingualDictionary();
}
