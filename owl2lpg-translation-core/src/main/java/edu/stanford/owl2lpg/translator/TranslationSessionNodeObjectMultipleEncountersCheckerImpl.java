package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TranslationSessionNodeObjectMultipleEncountersCheckerImpl
    implements TranslationSessionNodeObjectMultipleEncountersChecker {

  @Inject
  public TranslationSessionNodeObjectMultipleEncountersCheckerImpl() {
  }

  @Override
  public boolean isMultipleEncounterNodeObject(Object o) {
    return o instanceof IRI
        || o instanceof OWLEntity
        || o instanceof OWLLiteral;
  }
}
