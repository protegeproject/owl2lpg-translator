package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;
import edu.stanford.owl2lpg.translator.UniqueNodeChecker;

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
    private final UniqueNodeChecker uniqueNodeChecker;

    @Inject
    public CsvExporterFactory(@Nonnull OntologyDocumentAxiomTranslator ontologyDocumentAxiomTranslator, @Nonnull NodesCsvWriterFactory nodesCsvWriterFactory, @Nonnull RelationshipsCsvWriterFactory relationshipsCsvWriterFactory, @Nonnull UniqueNodeChecker uniqueNodeChecker) {
        this.ontologyDocumentAxiomTranslator = checkNotNull(ontologyDocumentAxiomTranslator);
        this.nodesCsvWriterFactory = checkNotNull(nodesCsvWriterFactory);
        this.relationshipsCsvWriterFactory = checkNotNull(relationshipsCsvWriterFactory);
        this.uniqueNodeChecker = checkNotNull(uniqueNodeChecker);
    }

    @Nonnull
    public CsvExporter create(@Nonnull Writer nodesCsvWriter,
                              @Nonnull Writer relationshipsCsvWriter) {
        return new CsvExporter(ontologyDocumentAxiomTranslator,
                               nodesCsvWriterFactory.create(nodesCsvWriter),
                               relationshipsCsvWriterFactory.create(relationshipsCsvWriter),
                               uniqueNodeChecker);
    }
}
