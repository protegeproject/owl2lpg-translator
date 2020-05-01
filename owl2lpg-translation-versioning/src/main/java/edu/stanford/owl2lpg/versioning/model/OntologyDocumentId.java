package edu.stanford.owl2lpg.versioning.model;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

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
    checkNotNull(identifier);
    return new AutoValue_OntologyDocumentId(identifier);
  }

  @Nonnull
  public abstract UUID getIdentifier();

  @Override
  public String toString() {
    return getIdentifier().toString();
  }
}
