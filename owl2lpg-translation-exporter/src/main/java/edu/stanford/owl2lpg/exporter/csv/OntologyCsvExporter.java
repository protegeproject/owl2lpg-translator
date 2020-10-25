package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.exporter.csv.internal.ProjectTranslator;
import edu.stanford.owl2lpg.exporter.csv.writer.Neo4jCsvWriter;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyCsvExporter {

  @Nonnull
  private final ProjectTranslator projectTranslator;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final Neo4jCsvWriter csvWriter;

  @Inject
  public OntologyCsvExporter(@Nonnull ProjectTranslator projectTranslator,
                             @Nonnull AxiomTranslator axiomTranslator,
                             @Nonnull Neo4jCsvWriter csvWriter) {
    this.projectTranslator = checkNotNull(projectTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.csvWriter = checkNotNull(csvWriter);
  }

  public void export(@Nonnull OWLOntology ontology) throws IOException {
    export(ontology, ProjectId.create(), BranchId.create(), OntologyDocumentId.create());
  }

  public void export(@Nonnull OWLOntology ontology,
                     @Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId documentId) throws IOException {
    var projectTranslation = projectTranslator.translate(ontology.getOntologyID(), projectId, branchId, documentId);
    csvWriter.writeTranslation(projectTranslation);
    ontology.getAxioms()
        .stream()
        .map(axiomTranslator::translate)
        .forEach(csvWriter::writeTranslation);
    csvWriter.printReport();
  }
}
