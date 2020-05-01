package edu.stanford.owl2lpg.client.read;

import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import org.neo4j.driver.Result;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ResultClassFrame {

  @Nonnull
  private final Result result;

  public ResultClassFrame(@Nonnull Result result) {
    this.result = checkNotNull(result);
  }

  public ClassFrame getClassFrame() {
    return null;
  }
}
