package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.ImmutableMultiset;
import edu.stanford.owl2lpg.translator.VersionedAxiomTranslator;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
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
  private final VersionedAxiomTranslator axiomTranslator;

  @Nonnull
  private final Neo4jCsvWriter csvWriter;

  @Inject
  public PerAxiomCsvExporter(@Nonnull VersionedAxiomTranslator axiomTranslator,
                             @Nonnull Neo4jCsvWriter csvWriter) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.csvWriter = checkNotNull(csvWriter);
  }

  public void export(@Nonnull OWLAxiom axiom) throws IOException {
    var translation = axiomTranslator.translate(axiom);
    csvWriter.writeTranslation(translation);
    csvWriter.flush();
  }

  public long getNodeCount() {
    return csvWriter.getNodeCount();
  }

  public long getEdgeCount() {
    return csvWriter.getEdgeCount();
  }

  public long getTrackedNodeCount() {
    return csvWriter.getTrackedNodeCount();
  }

  public long getTrackedEdgeCount() {
    return csvWriter.getTrackedEdgeCount();
  }

  public ImmutableMultiset<NodeLabels> getNodeLabelsMultiset() {
    return csvWriter.getNodeLabelsMultiset();
  }

  public ImmutableMultiset<EdgeLabel> getEdgeLabelMultiset() {
    return csvWriter.getEdgeLabelMultiset();
  }
}
