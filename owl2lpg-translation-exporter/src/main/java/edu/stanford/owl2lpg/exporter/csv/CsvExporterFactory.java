package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;
import edu.stanford.owl2lpg.translator.ProjectBranchTranslator;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

@Deprecated
public class CsvExporterFactory {

  @Nonnull
  private final OntologyDocumentAxiomTranslator ontologyDocumentAxiomTranslator;

  @Nonnull
  private final ProjectBranchTranslator projectBranchTranslator;

  @Nonnull
  private final NodesCsvWriterFactory nodesCsvWriterFactory;

  @Nonnull
  private final RelationshipsCsvWriterFactory relationshipsCsvWriterFactory;

  @Nonnull
  private final NodeTracker nodeTracker;

  @Nonnull
  final EdgeTracker edgeTracker;

  @Inject
  public CsvExporterFactory(@Nonnull OntologyDocumentAxiomTranslator ontologyDocumentAxiomTranslator,
                            @Nonnull ProjectBranchTranslator projectBranchTranslator,
                            @Nonnull NodesCsvWriterFactory nodesCsvWriterFactory,
                            @Nonnull RelationshipsCsvWriterFactory relationshipsCsvWriterFactory,
                            @Nonnull NodeTracker nodeTracker,
                            @Nonnull EdgeTracker edgeTracker) {
    this.ontologyDocumentAxiomTranslator = checkNotNull(ontologyDocumentAxiomTranslator);
    this.projectBranchTranslator = checkNotNull(projectBranchTranslator);
    this.nodesCsvWriterFactory = checkNotNull(nodesCsvWriterFactory);
    this.relationshipsCsvWriterFactory = checkNotNull(relationshipsCsvWriterFactory);
    this.nodeTracker = checkNotNull(nodeTracker);
    this.edgeTracker = checkNotNull(edgeTracker);
  }

  @Nonnull
  public CsvExporter create(@Nonnull Writer nodesCsvWriter,
                            @Nonnull Writer relationshipsCsvWriter) {
    return new CsvExporter(ontologyDocumentAxiomTranslator,
        projectBranchTranslator,
        nodesCsvWriterFactory.create(nodesCsvWriter),
        relationshipsCsvWriterFactory.create(relationshipsCsvWriter),
        nodeTracker, edgeTracker);
  }
}
