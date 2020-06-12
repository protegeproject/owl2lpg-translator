package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.visitors.NodeIdProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import java.security.MessageDigest;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DigestNodeIdProvider implements NodeIdProvider {

  @Nonnull
  private final MessageDigest messageDigest;

  public DigestNodeIdProvider(@Nonnull MessageDigest messageDigest) {
    this.messageDigest = checkNotNull(messageDigest);
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
    } else if (o instanceof OWLLiteral) {
      var literal = (OWLLiteral) o;
      var s = literal.getLiteral() + literal.getDatatype().toStringID() + literal.getLang();
      return createNodeIdFromObjectString(s);
    }
    return createNodeIdFromObjectString(o.toString());
  }

  private NodeId createNodeIdFromObjectString(String objectString) {
    var hash = messageDigest.digest(objectString.getBytes());
    return NodeId.create(bytesToHex(hash));
  }

  private static String bytesToHex(byte[] hash) {
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < hash.length; i++) {
      String hex = Integer.toHexString(0xff & hash[i]);
      if (hex.length() == 1) hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
