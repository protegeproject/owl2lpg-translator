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
public abstract class OntologyDocumentId {

  @Nonnull
  public static OntologyDocumentId create(@Nonnull String identifier) {
    return new AutoValue_OntologyDocumentId(identifier);
  }

  @Nonnull
  public static OntologyDocumentId create() {
    return create(UUID.randomUUID().toString());
  }

  @Nonnull
  public abstract String getIdentifier();

  @Nonnull
  public String printAsString() {
    return "\"" + getIdentifier() + "\"";
  }

  /**
   * Faster implementation of hashcode.  The identifier uniquely determines the hashcode
   * and this is cached by the JVM.
   */
  @Override
  public final int hashCode() {
    return getIdentifier().hashCode();
  }
}
