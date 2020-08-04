package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.OWLLiteral2;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IdFormatCheckerImpl implements IdFormatChecker {

  @Inject
  public IdFormatCheckerImpl() {
  }

  @Override
  public IdFormat getIdFormatFor(Object o) {
    if (o instanceof IRI || o instanceof OWLEntity || o instanceof OWLLiteral2) {
      return IdFormat.DIGEST;
    } else {
      return IdFormat.NUMBER;
    }
  }
}
