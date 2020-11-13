package edu.stanford.owl2lpg.translator.shared;

import com.google.common.hash.HashFunction;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BytesDigester {

  @Nonnull
  private final HashFunction hashFunction;

  @Inject
  public BytesDigester(HashFunction hashFunction) {
    this.hashFunction = checkNotNull(hashFunction);
  }

  @Nonnull
  public String getDigestString(byte[] bytes) {
    return hashFunction.hashBytes(bytes).toString();
  }
}
