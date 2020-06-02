package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.*;

import javax.inject.Inject;

public class TranslationSessionNodeObjectSingleEncounterCheckerImpl implements TranslationSessionNodeObjectSingleEncounterChecker {

    @Inject
    public TranslationSessionNodeObjectSingleEncounterCheckerImpl() {
    }

    @Override
    public boolean isSingleEncounterNodeObject(Object o) {
        return o instanceof OWLAxiom;
    }
}
