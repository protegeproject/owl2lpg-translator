package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Represents the branch identifier.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class BranchId {

  @Nonnull
  public static BranchId create(@Nonnull String identifier) {
    return new AutoValue_BranchId(identifier);
  }

  @Nonnull
  public static BranchId create() {
    return create(UUID.randomUUID().toString());
  }

  @Nonnull
  public abstract String getIdentifier();

  @Nonnull
  public String printAsString() {
    return "\"" + getIdentifier() + "\"";
  }

  @Override
  public int hashCode() {
    return getIdentifier().hashCode();
  }
}
