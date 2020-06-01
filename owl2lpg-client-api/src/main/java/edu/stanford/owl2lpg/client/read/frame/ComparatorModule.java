package edu.stanford.owl2lpg.client.read.frame;

import dagger.Module;
import dagger.Provides;
import edu.stanford.bmir.protege.web.shared.frame.PropertyValue;

import java.util.Comparator;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ComparatorModule {

  @Provides
  public Comparator<PropertyValue> provideComparator() {
    return PropertyValue::compareTo;
  }
}
