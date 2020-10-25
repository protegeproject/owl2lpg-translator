package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.exporter.csv.writer.Neo4jCsvWriter;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PerAxiomCsvExporter {

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final Neo4jCsvWriter csvWriter;

  @Inject
  public PerAxiomCsvExporter(@Nonnull AxiomTranslator axiomTranslator,
                             @Nonnull Neo4jCsvWriter csvWriter) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.csvWriter = checkNotNull(csvWriter);
  }

  public void export(@Nonnull OWLAxiom axiom) throws IOException {
    var translation = axiomTranslator.translate(axiom);
    csvWriter.writeTranslation(translation);
    csvWriter.flush();

    var console = new PrintWriter(System.out);

    console.printf("\nNodes: %,d\n\n", csvWriter.getNodeCount());
    csvWriter.getNodeLabelsMultiset()
        .forEachEntry((nodeLabels, count) ->
            console.printf("    Node   %-60s %,10d\n", nodeLabels.toNeo4jLabel(), count));

    console.printf("\nRelationships: %,d\n\n", csvWriter.getEdgeCount());
    csvWriter.getEdgeLabelMultiset()
        .forEachEntry((edgeLabel, count) ->
            console.printf("    Rel    %-36s %,10d\n", edgeLabel.toNeo4jLabel(), count));

    console.flush();
  }
}
