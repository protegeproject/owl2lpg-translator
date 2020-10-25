package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.exporter.csv.internal.ProjectTranslator;
import edu.stanford.owl2lpg.exporter.csv.writer.Neo4jCsvWriter;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PerAxiomCsvExporter {

  @Nonnull
  private final ProjectTranslator projectTranslator;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final Neo4jCsvWriter csvWriter;

  @Inject
  public PerAxiomCsvExporter(@Nonnull ProjectTranslator projectTranslator,
                             @Nonnull AxiomTranslator axiomTranslator,
                             @Nonnull Neo4jCsvWriter csvWriter) {
    this.projectTranslator = checkNotNull(projectTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.csvWriter = checkNotNull(csvWriter);
  }

  public void export(@Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId documentId) throws IOException {
    var translation = projectTranslator.translate(projectId, branchId, documentId);
    csvWriter.writeTranslation(translation);
  }

  public void export(@Nonnull OWLAxiom axiom) throws IOException {
    var translation = axiomTranslator.translate(axiom);
    csvWriter.writeTranslation(translation);
  }

  public void printReport() {
    csvWriter.printReport();
  }
}
