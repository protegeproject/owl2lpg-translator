package edu.stanford.owl2lpg.translator.shared;

import org.semanticweb.binaryowl.stream.BinaryOWLOutputStream;
import org.semanticweb.binaryowl.stream.TreeSetTransformer;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.DataOutput;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BinaryOwlOutputStreamFactory {

  @Inject
  public BinaryOwlOutputStreamFactory() {
  }

  @Nonnull
  public BinaryOWLOutputStream createOutputStream(@Nonnull DataOutput dataOutput) {
    return new BinaryOWLOutputStream(dataOutput, new TreeSetTransformer());
  }
}
