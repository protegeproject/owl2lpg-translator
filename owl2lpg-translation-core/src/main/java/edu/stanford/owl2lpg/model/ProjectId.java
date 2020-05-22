package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Represents the project identifier.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ProjectId {

  @Nonnull
  public static ProjectId create(@Nonnull UUID identifier) {
    return new AutoValue_ProjectId(identifier);
  }

  @Nonnull
  public static ProjectId create() {
    return create(UUID.randomUUID());
  }

  public abstract UUID getIdentifier();

  @Override
  public String toString() {
    return getIdentifier().toString();
  }
}
