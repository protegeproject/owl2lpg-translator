package edu.stanford.owl2lpg.translator.shared;

import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyObjectDigester {

  @Nonnull
  private final OntologyObjectSerializer ontologyObjectSerializer;

  @Nonnull
  private final BytesDigester bytesDigester;

  @Inject
  public OntologyObjectDigester(@Nonnull OntologyObjectSerializer ontologyObjectSerializer,
                                @Nonnull BytesDigester bytesDigester) {
    this.ontologyObjectSerializer = checkNotNull(ontologyObjectSerializer);
    this.bytesDigester = checkNotNull(bytesDigester);
  }

  @Nonnull
  public String getDigest(@Nonnull OWLObject owlObject) {
    var bytes = ontologyObjectSerializer.serialize(owlObject);
    return bytesDigester.getDigestString(bytes);
  }

  @Nonnull
  public String getDigest(@Nonnull Collection<? extends OWLObject> owlObjects) {
    var bytes = ontologyObjectSerializer.serialize(owlObjects);
    return bytesDigester.getDigestString(bytes);
  }
}
