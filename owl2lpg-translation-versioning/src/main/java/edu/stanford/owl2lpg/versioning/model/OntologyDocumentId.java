package edu.stanford.owl2lpg.versioning.model;

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
public abstract class OntologyDocumentId {

  @Nonnull
  public static OntologyDocumentId create(@Nonnull UUID identifier) {
    return new AutoValue_OntologyDocumentId(identifier);
  }

  @Nonnull
  public static OntologyDocumentId create() {
    return create(UUID.randomUUID());
  }

  @Nonnull
  public abstract UUID getIdentifier();

  @Override
  public String toString() {
    return getIdentifier().toString();
  }
}
