package edu.stanford.owl2lpg.translator.internal;

import com.google.common.hash.HashFunction;
import com.google.common.primitives.Ints;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.NodeIdProvider;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectSerializer;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class HashNodeIdProvider implements NodeIdProvider {

  @Nonnull
  private final OntologyObjectSerializer ontologyObjectSerializer;

  @Nonnull
  private final HashFunction hashFunction;

  @Nonnull
  private final AtomicInteger counter = new AtomicInteger();

  @Inject
  public HashNodeIdProvider(@Nonnull OntologyObjectSerializer ontologyObjectSerializer,
                            @Nonnull HashFunction hashFunction) {
    this.ontologyObjectSerializer = checkNotNull(ontologyObjectSerializer);
    this.hashFunction = checkNotNull(hashFunction);
  }

  @Override
  public NodeId getId(Object o) {
    var digest = "";
    if (o instanceof OWLObject) {
      var bytes = ontologyObjectSerializer.serialize((OWLObject) o);
      if (o instanceof OWLClassExpression || o instanceof OWLPropertyExpression) {
        digest = (o instanceof OWLEntity) ?
            getDigestString(bytes) :
            getDigestString(bytes) + counter.getAndIncrement();
      } else {
        digest = getDigestString(bytes);
      }
    } else {
      var bytes = Ints.toByteArray(o.hashCode());
      digest = getDigestString(bytes);
    }
    return NodeId.create(digest);
  }

  @Nonnull
  private String getDigestString(byte[] bytes) {
    return hashFunction.hashBytes(bytes).toString();
  }
}
