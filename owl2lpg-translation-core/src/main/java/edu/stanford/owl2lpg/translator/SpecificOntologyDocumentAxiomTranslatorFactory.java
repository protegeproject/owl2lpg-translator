package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.EdgeFactory;
import edu.stanford.owl2lpg.model.OntologyDocumentId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@Deprecated
@TranslationSessionScope
public class SpecificOntologyDocumentAxiomTranslatorFactory {

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final OntologyDocumentIdNodeFactory ontologyDocumentNodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Inject
  public SpecificOntologyDocumentAxiomTranslatorFactory(@Nonnull AxiomTranslator axiomTranslator,
                                                        @Nonnull OntologyDocumentIdNodeFactory ontologyDocumentNodeFactory,
                                                        @Nonnull EdgeFactory edgeFactory) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.ontologyDocumentNodeFactory = checkNotNull(ontologyDocumentNodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
  }

  @Nonnull
  public SpecificOntologyDocumentAxiomTranslator createTranslator(@Nonnull OntologyDocumentId ontologyDocumentId) {
    return new SpecificOntologyDocumentAxiomTranslator(ontologyDocumentId,
        axiomTranslator,
        ontologyDocumentNodeFactory,
        edgeFactory);
  }
}
