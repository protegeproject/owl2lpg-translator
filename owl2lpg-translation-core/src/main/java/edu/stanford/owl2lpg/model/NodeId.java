package edu.stanford.owl2lpg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.primitives.Longs;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

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
    return create(Longs.toByteArray(numberId));
  }

  public static NodeId create(String stringId) {
    return create(stringId.getBytes());
  }

  @JsonIgnore
  @Nonnull
  public abstract byte[] getBytes();

  @JsonValue
  public String asString() {
    return new String(getBytes(), StandardCharsets.UTF_8);
  }

  @Override
  public String toString() {
    return "NodeId_" + asString();
  }
}
