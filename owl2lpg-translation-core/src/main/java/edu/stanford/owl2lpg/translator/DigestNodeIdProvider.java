package edu.stanford.owl2lpg.translator;

import com.google.common.hash.HashFunction;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.visitors.OWLLiteral2;
import edu.stanford.owl2lpg.translator.visitors.NodeIdProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@SuppressWarnings("UnstableApiUsage")
public class DigestNodeIdProvider implements NodeIdProvider {

  @Nonnull
  private final HashFunction hashFunction;

  public DigestNodeIdProvider(@Nonnull HashFunction hashFunction) {
    this.hashFunction = checkNotNull(hashFunction);
  }

  @Override
  public NodeId getId(Object o) {
    if (o instanceof IRI) {
      var s = ((IRI) o).toString();
      return createNodeIdFromObjectString(s);
    } else if (o instanceof OWLEntity) {
      var entity = (OWLEntity) o;
      var s = entity.getEntityType().getName() + entity.getIRI().toString();
      return createNodeIdFromObjectString(s);
    } else if (o instanceof OWLLiteral2) {
      var literal = (OWLLiteral2) o;
      var s = literal.getLiteral() + literal.getDatatype() + literal.getLanguage();
      return createNodeIdFromObjectString(s);
    }
    return createNodeIdFromObjectString(o.toString());
  }

  private NodeId createNodeIdFromObjectString(String objectString) {
    var hash = hashFunction
        .hashString(objectString, StandardCharsets.UTF_8)
        .toString();
    return NodeId.create(hash);
  }
}
