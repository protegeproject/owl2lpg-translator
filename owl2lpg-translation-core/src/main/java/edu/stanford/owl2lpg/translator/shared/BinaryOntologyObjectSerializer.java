package edu.stanford.owl2lpg.translator.shared;

import com.google.common.io.ByteStreams;
import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BinaryOntologyObjectSerializer implements OntologyObjectSerializer {

  @Nonnull
  private final BinaryOwlOutputStreamFactory factory;

  @Inject
  public BinaryOntologyObjectSerializer(@Nonnull BinaryOwlOutputStreamFactory factory) {
    this.factory = checkNotNull(factory);
  }

  @Nonnull
  @Override
  public byte[] serialize(@Nonnull OWLObject owlObject) {
    try {
      var byteArrayDataOutput = ByteStreams.newDataOutput();
      var outputStream = factory.createOutputStream(byteArrayDataOutput);
      outputStream.writeOWLObject(owlObject);
      return byteArrayDataOutput.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
