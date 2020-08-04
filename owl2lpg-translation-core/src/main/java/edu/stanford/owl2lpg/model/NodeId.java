package edu.stanford.owl2lpg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.hash.HashCode;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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
    var bb = ByteBuffer.allocate(8);
    bb.putLong(numberId);
    return create(bb.array());
  }

  public static NodeId create(String stringId) {
    var bytes = stringId.getBytes(Charset.defaultCharset());
    return create(bytes);
  }

  @JsonIgnore
  @Nonnull
  public abstract byte[] getBytes();

  @JsonValue
  public String asString() {
    return HashCode.fromBytes(getBytes()).toString();
  }

  @Override
  public String toString() {
    return "NodeId_" + asString();
  }
}
