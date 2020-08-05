package edu.stanford.owl2lpg.translator;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.EdgeFactory;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM;

@Deprecated
public class SpecificOntologyDocumentAxiomTranslator {

  @Nonnull
  private final OntologyDocumentId ontologyDocumentId;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Nullable
  private Node ontologyDocumentNode;

  @Inject
  public SpecificOntologyDocumentAxiomTranslator(@Nonnull OntologyDocumentId ontologyDocumentId,
                                                 @Nonnull AxiomTranslator axiomTranslator,
                                                 @Nonnull OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory,
                                                 @Nonnull EdgeFactory edgeFactory) {
    this.ontologyDocumentId = checkNotNull(ontologyDocumentId);
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.ontologyDocumentIdNodeFactory = checkNotNull(ontologyDocumentIdNodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLAxiom axiom) {
    if (ontologyDocumentNode == null) {
      ontologyDocumentNode = ontologyDocumentIdNodeFactory.createOntologyDocumentNode(ontologyDocumentId);
    }
    var axiomTranslation = axiomTranslator.translate(axiom);
    return Translation.create(ontologyDocumentId,
        ontologyDocumentNode,
        ImmutableList.of(edgeFactory.createEdge(ontologyDocumentNode, axiomTranslation.getMainNode(), AXIOM)),
        ImmutableList.of(axiomTranslation));
  }
}
