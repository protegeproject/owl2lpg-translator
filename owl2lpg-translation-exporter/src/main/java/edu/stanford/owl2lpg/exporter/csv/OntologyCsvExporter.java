package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableCollection;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyCsvExporter {

  @Nonnull
  private final ImmutableCollection<OWLAxiom> axioms;

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontologyDocumentId;

  @Inject
  public OntologyCsvExporter(@Nonnull ImmutableCollection<OWLAxiom> axioms,
                             @Nonnull ProjectId projectId,
                             @Nonnull BranchId branchId,
                             @Nonnull OntologyDocumentId ontologyDocumentId) {
    this.axioms = checkNotNull(axioms);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontologyDocumentId = checkNotNull(ontologyDocumentId);
  }

  /**
   * Export the ontology to the specified nodes writer and relationships writer.
   * The processing messages will be display on the standard output stream.
   */
  public void export(@Nonnull Writer nodesCsvWriter,
                     @Nonnull Writer edgesCsvWriter) throws IOException {
    export(nodesCsvWriter, edgesCsvWriter, new PrintWriter(System.out));
  }

  /**
   * Export the ontology to the specified nodes writer and relationships writer,
   * with a user-defined console stream to display the processing messages.
   */
  public void export(@Nonnull Writer nodesCsvWriter,
                     @Nonnull Writer edgesCsvWriter,
                     @Nonnull PrintWriter console) throws IOException {

    var exporterFactory = DaggerCsvExporterComponent.create().getCsvExporterFactory();
    var exporter = exporterFactory.create(nodesCsvWriter, edgesCsvWriter);

    console.printf("Axioms: %,d\n", axioms.size());

    var stopwatch = Stopwatch.createStarted();
    int percentageComplete = 0;
    int axiomCounter = 0;
    for (var ax : axioms) {
      axiomCounter++;
      var percent = (axiomCounter * 100) / axioms.size();
      if (percent != percentageComplete) {
        percentageComplete = percent;
        console.printf("%3d%% [%,d nodes, %,d edges]\n", percentageComplete,
            exporter.getNodeCount(),
            exporter.getEdgeCount());
        console.flush();
      }
      exporter.write(ontologyDocumentId, ax);
    }
    exporter.write(projectId, branchId, ontologyDocumentId);
    exporter.flush();

    console.printf("\nNodes: %,d\n\n", exporter.getNodeCount());

    var nodeLabelsMultiset = exporter.getNodeLabelsMultiset();
    nodeLabelsMultiset
        .forEachEntry((nodeLabels, count) -> {
          console.printf("    Node   %-60s %,10d\n", nodeLabels.printLabels(), count);
        });


    console.printf("\nRelationships: %,d\n\n", exporter.getEdgeCount());

    var edgeLabelMultiset = exporter.getEdgeLabelMultiset();
    edgeLabelMultiset
        .forEachEntry((edgeLabel, count) -> {
          console.printf("    Rel    %-36s %,10d\n", edgeLabel.printLabel(), count);
        });

    console.printf("\nExport complete in %,d ms\n", stopwatch.elapsed().toMillis());
    console.flush();
  }
}
