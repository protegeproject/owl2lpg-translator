package edu.stanford.owl2lpg.translator;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.EdgeFactory;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_SIGNATURE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyDocumentAxiomTranslator {

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final EntityTranslator entityTranslator;

  @Nonnull
  private final OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Inject
  public OntologyDocumentAxiomTranslator(@Nonnull AxiomTranslator axiomTranslator,
                                         @Nonnull EntityTranslator entityTranslator,
                                         @Nonnull OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory,
                                         @Nonnull EdgeFactory edgeFactory) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.ontologyDocumentIdNodeFactory = checkNotNull(ontologyDocumentIdNodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
  }

  @Nonnull
  public Translation translate(@Nonnull OntologyDocumentId ontologyDocumentId,
                               @Nonnull OWLAxiom axiom) {
    checkNotNull(ontologyDocumentId);
    var edges = new ImmutableList.Builder<Edge>();
    var translations = new ImmutableList.Builder<Translation>();
    var axiomTranslation = axiomTranslator.translate(axiom);
    translations.add(axiomTranslation);
    var ontologyDocumentNode = ontologyDocumentIdNodeFactory.createOntologyDocumentNode(ontologyDocumentId);
    edges.add(edgeFactory.createEdge(ontologyDocumentNode, axiomTranslation.getMainNode(), AXIOM));
    axiom.getSignature()
        .stream()
        .map(entityTranslator::translate)
        .forEach(t -> edges.add(edgeFactory.createEdge(ontologyDocumentNode, t.getMainNode(), ENTITY_SIGNATURE)));
    return Translation.create(ontologyDocumentId,
        ontologyDocumentNode,
        edges.build(),
        ImmutableList.of(axiomTranslation));
  }
}
