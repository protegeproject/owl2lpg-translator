package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.*;

import javax.inject.Inject;

public class TranslationSessionUniqueEncounterNodeCheckerImpl implements TranslationSessionUniqueEncounterNodeChecker {

    @Inject
    public TranslationSessionUniqueEncounterNodeCheckerImpl() {
    }

    @Override
    public boolean isTranslationSessionUniqueEncounterNodeObject(Object o) {
        return !(o instanceof OWLAxiom);
    }
}
