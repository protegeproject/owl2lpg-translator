package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.inject.Inject;

public class SingleEncounterNodeCheckerImpl implements SingleEncounterNodeChecker {

  @Inject
  public SingleEncounterNodeCheckerImpl() {
  }

  @Override
  public boolean isSingleEncounterNodeObject(Object o) {
    return o instanceof OWLAxiom
        || o instanceof OWLAnnotation;
  }
}
