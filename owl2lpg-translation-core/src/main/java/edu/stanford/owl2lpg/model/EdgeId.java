package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;

import java.nio.charset.Charset;

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

  public static EdgeId create(String stringId) {
    return create(stringId.getBytes());
  }

  public abstract byte[] getBytes();

  public String asString() {
    return new String(getBytes(), Charset.defaultCharset());
  }

  @Override
  public String toString() {
    return "EdgeId_" + asString();
  }
}
