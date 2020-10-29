package edu.stanford.owl2lpg.translator.shared;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Represents the project identifier.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Deprecated
@AutoValue
public abstract class ProjectId {

  @Nonnull
  public static ProjectId create(@Nonnull String identifier) {
    return new AutoValue_ProjectId(identifier);
  }

  @Nonnull
  public static ProjectId create() {
    return create(UUID.randomUUID().toString());
  }

  @Nonnull
  public abstract String getIdentifier();

  @Nonnull
  public String toQuotedString() {
    return "\"" + getIdentifier() + "\"";
  }

  @Override
  public int hashCode() {
    return getIdentifier().hashCode();
  }
}
