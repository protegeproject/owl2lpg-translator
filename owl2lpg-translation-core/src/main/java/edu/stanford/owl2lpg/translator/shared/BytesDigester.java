package edu.stanford.owl2lpg.translator.shared;

import com.google.common.hash.Hashing;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BytesDigester {

  @Inject
  public BytesDigester() {
  }

  @Nonnull
  public String getDigestString(byte[] bytes) {
    return Hashing.sha256().hashBytes(bytes).toString();
  }
}
