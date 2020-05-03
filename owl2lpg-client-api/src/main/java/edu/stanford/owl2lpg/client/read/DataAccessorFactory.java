package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.client.Database;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataAccessorFactory {

  private final ImmutableList<FrameAccessorFactory> frameAccessorFactories;

  private final ImmutableList<HierarchyAccessorFactory> hierarchyAccessorFactories;

  public DataAccessorFactory(@Nonnull ImmutableList<FrameAccessorFactory> frameAccessorFactories,
                             @Nonnull ImmutableList<HierarchyAccessorFactory> hierarchyAccessorFactories) {
    this.frameAccessorFactories = checkNotNull(frameAccessorFactories);
    this.hierarchyAccessorFactories = checkNotNull(hierarchyAccessorFactories);
  }

  public <T> FrameAccessor<T> getFrameAccessor(@Nonnull Class<T> frameClass,
                                               @Nonnull Database database,
                                               @Nonnull Session session) {
    for (var frameAccessorFactory : frameAccessorFactories) {
      if (frameAccessorFactory.isAccessorFor(frameClass)) {
        return frameAccessorFactory.getAccessor(database, session);
      }
    }
    throw new IllegalArgumentException(format("Unable to get frame accessor for %s", frameClass));
  }

  public <T> HierarchyAccessor<T> getHierarchyAccessor(@Nonnull Class<T> hierarchyClass,
                                                       @Nonnull Database database,
                                                       @Nonnull Session session) {
    for (var hierarchyAccessorFactory : hierarchyAccessorFactories) {
      if (hierarchyAccessorFactory.isAccessorFor(hierarchyClass)) {
        return hierarchyAccessorFactory.getAccessor(database, session);
      }
    }
    throw new IllegalArgumentException(format("Unable to get hierarchy accessor for %s", hierarchyClass));
  }
}
