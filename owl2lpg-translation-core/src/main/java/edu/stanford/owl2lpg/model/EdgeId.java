package edu.stanford.owl2lpg.model;

import at.favre.lib.bytes.Bytes;
import com.google.auto.value.AutoValue;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class EdgeId {

  private String cachedString;

  @SuppressWarnings("mutable")
  public static EdgeId create(byte[] bytes) {
    return new AutoValue_EdgeId(bytes);
  }

  public static EdgeId create(long numberId) {
    return create(Bytes.empty()
        .append(numberId)
        .array());
  }

  public static EdgeId create(String stringId) {
    return create(Bytes.empty()
        .append(stringId)
        .array());
  }

  public abstract byte[] getBytes();

  public String asString() {
    if (cachedString == null) {
      cachedString = Bytes.wrap(getBytes()).encodeHex();
    }
    return cachedString;
  }

  @Override
  public String toString() {
    return "EdgeId_" + asString();
  }
}
