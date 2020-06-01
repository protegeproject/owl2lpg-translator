package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.*;

import javax.inject.Inject;

public class UniqueNodeCheckerImpl implements UniqueNodeChecker {

    @Inject
    public UniqueNodeCheckerImpl() {
    }

    @Override
    public boolean isUniqueNode(Object o) {
        return !(o instanceof OWLAxiom);
    }
}
