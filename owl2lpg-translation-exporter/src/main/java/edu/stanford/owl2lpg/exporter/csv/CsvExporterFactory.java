package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;
import edu.stanford.owl2lpg.translator.TranslationSessionUniqueEncounterNodeChecker;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

public class CsvExporterFactory {

    @Nonnull
    private final OntologyDocumentAxiomTranslator ontologyDocumentAxiomTranslator;

    @Nonnull
    private final NodesCsvWriterFactory nodesCsvWriterFactory;

    @Nonnull
    private final RelationshipsCsvWriterFactory relationshipsCsvWriterFactory;

    @Nonnull
    private final TranslationSessionUniqueEncounterNodeChecker translationSessionUniqueEncounterNodeChecker;

    @Inject
    public CsvExporterFactory(@Nonnull OntologyDocumentAxiomTranslator ontologyDocumentAxiomTranslator, @Nonnull NodesCsvWriterFactory nodesCsvWriterFactory, @Nonnull RelationshipsCsvWriterFactory relationshipsCsvWriterFactory, @Nonnull TranslationSessionUniqueEncounterNodeChecker translationSessionUniqueEncounterNodeChecker) {
        this.ontologyDocumentAxiomTranslator = checkNotNull(ontologyDocumentAxiomTranslator);
        this.nodesCsvWriterFactory = checkNotNull(nodesCsvWriterFactory);
        this.relationshipsCsvWriterFactory = checkNotNull(relationshipsCsvWriterFactory);
        this.translationSessionUniqueEncounterNodeChecker = checkNotNull(translationSessionUniqueEncounterNodeChecker);
    }

    @Nonnull
    public CsvExporter create(@Nonnull Writer nodesCsvWriter,
                              @Nonnull Writer relationshipsCsvWriter) {
        return new CsvExporter(ontologyDocumentAxiomTranslator,
                               nodesCsvWriterFactory.create(nodesCsvWriter),
                               relationshipsCsvWriterFactory.create(relationshipsCsvWriter),
                               translationSessionUniqueEncounterNodeChecker);
    }
}
