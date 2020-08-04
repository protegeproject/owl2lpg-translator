package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;
import com.google.common.hash.HashCode;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class EdgeId {

  @SuppressWarnings("mutable")
  public static EdgeId create(byte[] bytes) {
    return new AutoValue_EdgeId(bytes);
  }

  public abstract byte[] getBytes();

  public String asString() {
    return HashCode.fromBytes(getBytes()).toString();
  }

  @Override
  public String toString() {
    return "EdgeId_" + asString();
  }
}
