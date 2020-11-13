package edu.stanford.owl2lpg.translator.internal;

import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.NodeIdProvider;
import edu.stanford.owl2lpg.translator.shared.BytesDigester;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectSerializer;
import org.jetbrains.annotations.NotNull;
import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DigestNodeIdProvider implements NodeIdProvider {

  @Nonnull
  private final OntologyObjectSerializer ontologyObjectSerializer;

  @Nonnull
  private final BytesDigester bytesDigester;

  @Inject
  public DigestNodeIdProvider(@Nonnull OntologyObjectSerializer ontologyObjectSerializer,
                              @Nonnull BytesDigester bytesDigester) {
    this.ontologyObjectSerializer = checkNotNull(ontologyObjectSerializer);
    this.bytesDigester = checkNotNull(bytesDigester);
  }

  @Override
  public NodeId getId(Object o) {
    byte[] bytes = (o instanceof OWLObject)
        ? ontologyObjectSerializer.serialize((OWLObject) o)
        : getBytes(o);
    return getNodeId(bytes);

  }

  @NotNull
  private byte[] getBytes(Object o) {
    return (o.hashCode() + "").getBytes();
  }

  @Nonnull
  private NodeId getNodeId(byte[] bytes) {
    var digest = bytesDigester.getDigestString(bytes);
    return NodeId.create(digest);
  }
}
