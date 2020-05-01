package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import edu.stanford.owl2lpg.versioning.translator.AxiomContextTranslator;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomStorer {

  @Nonnull
  private final Database database;

  @Nonnull
  private final AxiomContextTranslator axiomContextTranslator;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  public AxiomStorer(@Nonnull Database database,
                     @Nonnull AxiomContextTranslator axiomContextTranslator,
                     @Nonnull AxiomTranslator axiomTranslator) {
    this.database = checkNotNull(database);
    this.axiomContextTranslator = checkNotNull(axiomContextTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
  }

  public void add(AxiomContext context, Collection<OWLAxiom> axioms) {
    writeAxiomContext(context);
    axioms.stream()
        .forEach(axiom -> {
          var axiomTranslation = axiomTranslator.translate(axiom);
          axiomTranslation.closure().forEach(database::insert);
          database.insert(createDocumentAxiomEdge(context, axiomTranslation));
        });
  }

  private void writeAxiomContext(AxiomContext context) {
    var contextTranslation = axiomContextTranslator.translate(context);
//    contextTranslation.nodes().forEach(database::insert);
    contextTranslation.edges().forEach(database::insert);
  }

  private Edge createDocumentAxiomEdge(AxiomContext context, Translation axiomTranslation) {
    return Edge.create(
        axiomContextTranslator.createOntologyDocumentNode(context.getOntologyDocumentId()),
        axiomTranslation.getMainNode(),
        EdgeLabels.AXIOM,
        Properties.empty());
  }
}
