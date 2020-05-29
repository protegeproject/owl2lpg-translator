package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Comparators;
import com.google.common.collect.ImmutableMultiset;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import edu.stanford.owl2lpg.translator.TranslatorComponent;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyCsvExporter {

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

    /**
     * Export the ontology to the specified nodes writer and relationships writer
     */
    public void export(@Nonnull Writer nodesCsvWriter,
                       @Nonnull Writer edgesCsvWriter) throws IOException {

        var exporterFactory = DaggerCsvExporterComponent.create().getCsvExporterFactory();
        var exporter = exporterFactory.create(nodesCsvWriter,
                                              edgesCsvWriter);
        var axioms = ontology.getAxioms();

        int logicalAxiomCount = ontology.getLogicalAxiomCount();
        console.printf("Logical axioms: %,d\n", logicalAxiomCount);
        int axiomCount = ontology.getAxiomCount();
        console.printf("Non-logical axioms: %,d\n", axiomCount - logicalAxiomCount);
        console.printf("Axioms: %,d\n", axiomCount);

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
