package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.translator.OntologyProjectTranslator;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyCsvExporter {

  @Nonnull
  private final OntologyProjectTranslator ontologyProjectTranslator;

  @Nonnull
  private final Neo4jCsvWriter csvWriter;

  @Inject
  public OntologyCsvExporter(@Nonnull OntologyProjectTranslator ontologyProjectTranslator,
                             @Nonnull Neo4jCsvWriter csvWriter) {
    this.ontologyProjectTranslator = checkNotNull(ontologyProjectTranslator);
    this.csvWriter = checkNotNull(csvWriter);
  }

  public void export(@Nonnull OWLOntology ontology) throws IOException {
    var translation = ontologyProjectTranslator.translate(ontology);
    csvWriter.writeTranslation(translation);
    csvWriter.flush();

    var console = new PrintWriter(System.out);

    console.printf("\nNodes: %,d\n\n", csvWriter.getNodeCount());
    csvWriter.getNodeLabelsMultiset()
        .forEachEntry((nodeLabels, count) ->
            console.printf("    Node   %-60s %,10d\n", nodeLabels.getNeo4jName(), count));

    console.printf("\nRelationships: %,d\n\n", csvWriter.getEdgeCount());
    csvWriter.getEdgeLabelMultiset()
        .forEachEntry((edgeLabel, count) ->
            console.printf("    Rel    %-36s %,10d\n", edgeLabel.getNeo4jName(), count));

    console.flush();
  }


}
