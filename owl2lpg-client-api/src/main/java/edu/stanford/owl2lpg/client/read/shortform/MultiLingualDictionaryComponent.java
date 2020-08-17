package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Component;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualDictionary;
import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;
import edu.stanford.owl2lpg.client.read.axiom.handlers.OwlDataFactoryModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    DatabaseModule.class,
    MultiLingualDictionaryContextModule.class,
    Neo4jMultiLingualShortFormModule.class,
    OwlDataFactoryModule.class,
    JsonMapperModule.class
})
@DatabaseSessionScope
public interface MultiLingualDictionaryComponent {

  MultiLingualDictionary getMultiLingualDictionary();
}
