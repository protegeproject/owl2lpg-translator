package edu.stanford.owl2lpg.translator.util;

import com.google.common.io.ByteStreams;
import org.semanticweb.binaryowl.stream.BinaryOWLOutputStream;
import org.semanticweb.binaryowl.stream.TreeSetTransformer;
import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class BinaryOntologyObjectSerializer implements OntologyObjectSerializer {

  @Inject
  public BinaryOntologyObjectSerializer() {
  }

  @Nonnull
  @Override
  public ByteArray getByteArray(@Nonnull OWLObject owlObject) {
    try {
      var byteArrayDataOutput = ByteStreams.newDataOutput();
      var outputStream = new BinaryOWLOutputStream(byteArrayDataOutput, new TreeSetTransformer());
      outputStream.writeOWLObject(owlObject);
      return ByteArray.of(byteArrayDataOutput.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
