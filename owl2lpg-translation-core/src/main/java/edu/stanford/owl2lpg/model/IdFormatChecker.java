package edu.stanford.owl2lpg.model;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Deprecated
public interface IdFormatChecker {

  enum IdFormat {
    DIGEST, NUMBER, STRING
  }

  IdFormat getIdFormatFor(Object o);
}
