package edu.stanford.owl2lpg.client;

import dagger.Module;
import dagger.Provides;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class DatabaseModule {

  @Nonnull
  private final Driver driver;

  public DatabaseModule(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  @Provides
  @DatabaseSessionScope
  public Session provideSession() {
    return driver.session();
  }
}
