package edu.stanford.owl2lpg.translator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class SpecificOntologyDocumentAxiomTranslator {

    @Nonnull
    private final AxiomTranslator axiomTranslator;

    @Nonnull
    private final OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory;

    @Nonnull
    private final OntologyDocumentId ontologyDocumentId;

    @Nullable
    private Node ontologyDocumentNode;

    @Inject
    public SpecificOntologyDocumentAxiomTranslator(@Nonnull AxiomTranslator axiomTranslator,
                                                   @Nonnull OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory,
                                                   @Nonnull OntologyDocumentId ontologyDocumentId) {
        this.axiomTranslator = checkNotNull(axiomTranslator);
        this.ontologyDocumentIdNodeFactory = checkNotNull(ontologyDocumentIdNodeFactory);
        this.ontologyDocumentId = checkNotNull(ontologyDocumentId);
    }

    @Nonnull
    public Translation translate(@Nonnull OWLAxiom axiom) {
        if(ontologyDocumentNode == null) {
            ontologyDocumentNode = ontologyDocumentIdNodeFactory.createOntologyDocumentNode(ontologyDocumentId);
        }
        var axiomTranslation = axiomTranslator.translate(axiom);
        return Translation.create(ontologyDocumentId,
                                  ontologyDocumentNode,
                                  ImmutableList.of(Edge.create(ontologyDocumentNode, axiomTranslation.getMainNode(), EdgeLabel.AXIOM)),
                                  ImmutableList.of(axiomTranslation));
    }
}
