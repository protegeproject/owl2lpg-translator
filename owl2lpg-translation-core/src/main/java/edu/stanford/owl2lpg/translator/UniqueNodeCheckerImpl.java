package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;

import javax.inject.Inject;

public class UniqueNodeCheckerImpl implements UniqueNodeChecker {

    @Inject
    public UniqueNodeCheckerImpl() {
    }

    @Override
    public boolean isUniqueNode(Object o) {
        return true;
    }
}
