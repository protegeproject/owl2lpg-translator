package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.OntologyDocumentId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@TranslationSessionScope
public class SpecificOntologyDocumentAxiomTranslatorFactory {

    @Nonnull
    private final AxiomTranslator axiomTranslator;

    @Nonnull
    private final OntologyDocumentIdNodeFactory ontologyDocumentNodeFactory;

    @Inject
    public SpecificOntologyDocumentAxiomTranslatorFactory(@Nonnull AxiomTranslator axiomTranslator, @Nonnull OntologyDocumentIdNodeFactory ontologyDocumentNodeFactory) {
        this.axiomTranslator = checkNotNull(axiomTranslator);
        this.ontologyDocumentNodeFactory = checkNotNull(ontologyDocumentNodeFactory);
    }

    @Nonnull
    public SpecificOntologyDocumentAxiomTranslator createTranslator(@Nonnull OntologyDocumentId ontologyDocumentId) {
        return new SpecificOntologyDocumentAxiomTranslator(axiomTranslator,
                                                           ontologyDocumentNodeFactory,
                                                           ontologyDocumentId);
    }
}
