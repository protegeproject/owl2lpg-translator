package edu.stanford.owl2lpg.model;

import at.favre.lib.bytes.Bytes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * Represents a construction of a node identifier.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class NodeId {

  @SuppressWarnings("mutable")
  public static NodeId create(byte[] bytes) {
    return new AutoValue_NodeId(bytes);
  }

  public static NodeId create(long numberId) {
    return create(Bytes.empty()
        .append(numberId)
        .array());
  }

  public static NodeId create(String stringId) {
    return create(Bytes.empty()
        .append(stringId)
        .array());
  }

  @JsonIgnore
  @Nonnull
  public abstract byte[] getBytes();

  @JsonValue
  public String asString() {
    return Bytes.wrap(getBytes()).encodeHex();
  }

  @Override
  public String toString() {
    return "NodeId_" + asString();
  }
}
