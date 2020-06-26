package edu.stanford.owl2lpg.client.read.axiom;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class IndexBundle {

  public static IndexBundle create(@Nonnull NodeIndex nodeIndex,
                                   @Nonnull DictionaryNameIndex dictionaryNameIndex) {
    return new AutoValue_IndexBundle(nodeIndex, dictionaryNameIndex);
  }

  @Nonnull
  public abstract NodeIndex getNodeIndex();

  @Nonnull
  public abstract DictionaryNameIndex getDictionaryNameIndex();
}
