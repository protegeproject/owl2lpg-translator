package edu.stanford.owl2lpg.client.read;

import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.DatabaseConnection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface HierarchyAccessorFactory<T> {

  HierarchyAccessor<T> getAccessor(Database database, DatabaseConnection connection);

  boolean isAccessorFor(Class<?> hierarchyClass);
}
