package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.client.Database;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class HierarchyAccessor<T> {

  @Nonnull
  private final Database database;

  @Nonnull
  private final Session session;

  private final List<Object> parameters = Lists.newArrayList();

  public HierarchyAccessor(@Nonnull Database database,
                           @Nonnull Session session) {
    this.database = checkNotNull(database);
    this.session = checkNotNull(session);
  }

  public HierarchyAccessor<T> setParameter(Object parameter) {
    parameters.add(parameter);
    return this;
  }
}
