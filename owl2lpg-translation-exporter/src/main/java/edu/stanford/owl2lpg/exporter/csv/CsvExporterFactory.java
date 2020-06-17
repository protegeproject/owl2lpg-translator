package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;

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
  private final ExportTracker<Node> nodeTracker;

  @Nonnull
  final ExportTracker<Edge> edgeTracker;

  @Inject
  public CsvExporterFactory(@Nonnull OntologyDocumentAxiomTranslator ontologyDocumentAxiomTranslator,
                            @Nonnull NodesCsvWriterFactory nodesCsvWriterFactory,
                            @Nonnull RelationshipsCsvWriterFactory relationshipsCsvWriterFactory,
                            @Nonnull ExportTracker<Node> nodeTracker,
                            @Nonnull ExportTracker<Edge> edgeTracker) {
    this.ontologyDocumentAxiomTranslator = checkNotNull(ontologyDocumentAxiomTranslator);
    this.nodesCsvWriterFactory = checkNotNull(nodesCsvWriterFactory);
    this.relationshipsCsvWriterFactory = checkNotNull(relationshipsCsvWriterFactory);
    this.nodeTracker = checkNotNull(nodeTracker);
    this.edgeTracker = checkNotNull(edgeTracker);
  }

  @Nonnull
  public CsvExporter create(@Nonnull Writer nodesCsvWriter,
                            @Nonnull Writer relationshipsCsvWriter) {
    return new CsvExporter(ontologyDocumentAxiomTranslator,
        nodesCsvWriterFactory.create(nodesCsvWriter),
        relationshipsCsvWriterFactory.create(relationshipsCsvWriter),
        nodeTracker, edgeTracker);
  }
}
