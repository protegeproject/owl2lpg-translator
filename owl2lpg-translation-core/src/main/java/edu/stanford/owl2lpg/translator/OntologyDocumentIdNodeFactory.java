package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;

public class OntologyDocumentIdNodeFactory {

    @Nonnull
    private final NodeFactory nodeFactory;

    @Inject
    public OntologyDocumentIdNodeFactory(@Nonnull NodeFactory nodeFactory) {
        this.nodeFactory = checkNotNull(nodeFactory);
    }

    @Nonnull
    public Node createOntologyDocumentNode(@Nonnull OntologyDocumentId docId) {
        return nodeFactory.createNode(
                docId,
                NodeLabels.ONTOLOGY_DOCUMENT,
                Properties.of(ONTOLOGY_DOCUMENT_ID,
                              docId.getIdentifier()));
    }
}
