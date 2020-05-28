package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.google.common.base.Stopwatch;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import edu.stanford.owl2lpg.translator.TranslatorComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
// TODO: Rename the class name
public class OntologyCsvExporter {

  private static final String NODES_CSV = "nodes.csv";

  private static final String RELATIONSHIPS_CSV = "relationships.csv";

  @Nonnull
  private final OntologyDocumentId ontologyDocumentId;

  @Nonnull
  private final OWLOntology ontology;

  @Nonnull
  private final PrintWriter console;

  public OntologyCsvExporter(@Nonnull OWLOntology ontology,
                             @Nonnull OntologyDocumentId ontologyDocumentId,
                             @Nonnull PrintWriter console) {
    this.ontologyDocumentId = checkNotNull(ontologyDocumentId);
    this.ontology = checkNotNull(ontology);
    this.console = checkNotNull(console);
  }

  public void export(@Nonnull Path outputDirectory) throws IOException {
    Files.createDirectories(outputDirectory);
    checkNotNull(ontology);
    checkNotNull(outputDirectory);
    TranslatorComponent translatorComponent = DaggerTranslatorComponent.create();
    var axiomTranslator = translatorComponent.getOntologyDocumentAxiomTranslator();
    var nodesCsvPath = outputDirectory.resolve(NODES_CSV);
    var relationshipsCsvPath = outputDirectory.resolve(RELATIONSHIPS_CSV);
    var nodesWriter = new CsvWriter<Node>(new CsvMapper(),
                                          createWriter(nodesCsvPath),
                                          new Neo4jNodeCsvSchema());
    var relationshipsWriter = new CsvWriter<Edge>(new CsvMapper(),
                                                  createWriter(relationshipsCsvPath),
                                                  new Neo4jRelationshipsCsvSchema());

    var exporter = new CsvExporter(axiomTranslator, nodesWriter, relationshipsWriter);
    var axioms = ontology.getAxioms();
    var stopwatch = Stopwatch.createStarted();
    int percentageComplete = 0;
    int axiomCounter = 0;
    for(var ax : axioms) {
      axiomCounter++;
      var percent = (axiomCounter * 100) / axioms.size();
      if(percent != percentageComplete) {
        percentageComplete = percent;
        console.printf("%3d%%\n", percentageComplete);
        console.flush();
      }
      exporter.write(ontologyDocumentId, ax);
    }
    console.printf("Exported %,d nodes\n", exporter.getNodeCount());
    console.printf("Exported %,d relationships\n", exporter.getEdgeCount());
    console.printf("Export complete in %,d ms\n", stopwatch.elapsed().toMillis());
    console.flush();
  }

  private static Writer createWriter(@Nonnull Path path) throws IOException {
    return new BufferedWriter(new FileWriter(path.toFile()));
  }
}
