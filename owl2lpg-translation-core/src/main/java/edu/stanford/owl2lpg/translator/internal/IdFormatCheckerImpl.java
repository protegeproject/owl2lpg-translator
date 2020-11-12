package edu.stanford.owl2lpg.translator.internal;

import edu.stanford.owl2lpg.model.IdFormatChecker;
import edu.stanford.owl2lpg.translator.visitors.OWLLiteral2;
import edu.stanford.owl2lpg.translator.visitors.OWLPropertyChain;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Deprecated
public class IdFormatCheckerImpl implements IdFormatChecker {

  @Inject
  public IdFormatCheckerImpl() {
  }

  @Override
  public IdFormat getIdFormatFor(Object o) {
    if (o instanceof IRI || o instanceof OWLEntity || o instanceof OWLLiteral2 || o instanceof OWLPropertyChain) {
      return IdFormat.DIGEST;
    } else {
      return IdFormat.NUMBER;
    }
  }
}
