package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Component;
import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;
import edu.stanford.owl2lpg.client.read.axiom.handlers.OwlDataFactoryModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    DatabaseModule.class,
    OwlDataFactoryModule.class,
    ObjectMapperModule.class,
    MultiLingualShortFormAccessorModule.class
})
@DatabaseSessionScope
public interface MultiLingualShortFormAccessorComponent {

  MultiLingualShortFormAccessor getMultiLingualShortFormAccessor();
}
