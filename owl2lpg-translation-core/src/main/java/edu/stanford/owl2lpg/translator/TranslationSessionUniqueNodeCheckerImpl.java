package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.*;

import javax.inject.Inject;

public class TranslationSessionUniqueNodeCheckerImpl implements TranslationSessionUniqueNodeChecker {

    @Inject
    public TranslationSessionUniqueNodeCheckerImpl() {
    }

    @Override
    public boolean isTranslationSessionUniqueNode(Object o) {
        return !(o instanceof OWLAxiom);
    }
}
