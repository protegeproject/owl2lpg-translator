package edu.stanford.owl2lpg.translator;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyDocumentAxiomTranslator {

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory;

  @Inject
  public OntologyDocumentAxiomTranslator(@Nonnull AxiomTranslator axiomTranslator,
                                         @Nonnull OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.ontologyDocumentIdNodeFactory = checkNotNull(ontologyDocumentIdNodeFactory);
  }

  @Nonnull
  public Translation translate(@Nonnull OntologyDocumentId ontologyDocumentId,
                               @Nonnull OWLAxiom axiom) {
    checkNotNull(ontologyDocumentId);
    var axiomTranslation = axiomTranslator.translate(axiom);
    var ontologyDocumentNode = ontologyDocumentIdNodeFactory.createOntologyDocumentNode(ontologyDocumentId);
    return Translation.create(ontologyDocumentId,
        ontologyDocumentNode,
        ImmutableList.of(Edge.create(ontologyDocumentNode, axiomTranslation.getMainNode(), AXIOM)),
        ImmutableList.of(axiomTranslation));
  }
}
