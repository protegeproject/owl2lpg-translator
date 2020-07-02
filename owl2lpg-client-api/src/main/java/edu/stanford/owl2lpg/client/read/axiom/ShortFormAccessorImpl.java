package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.client.read.frame.Parameters;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.read.axiom.CypherQueries.SHORT_FORMS_QUERY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ShortFormAccessorImpl implements ShortFormAccessor {

  @Nonnull
  private final Session session;

  @Inject
  public ShortFormAccessorImpl(@Nonnull Session session) {
    this.session = checkNotNull(session);
  }

  @Override
  public ShortFormIndex getShortFormIndex(AxiomContext context, IRI entityIri) {
    var args = Parameters.forEntityIri(context, entityIri);
    return session.readTransaction(tx -> {
      var result = tx.run(SHORT_FORMS_QUERY, args);
      var shortFormIndexBuilder = new ShortFormIndexImpl.Builder();
      while (result.hasNext()) {
        var row = result.next().asMap();
        var propertyNode = (Node) row.get("annotationProperty");
        var literalNode = (Node) row.get("value");
        shortFormIndexBuilder.add(propertyNode, literalNode);
      }
      return shortFormIndexBuilder.build();
    });
  }
}
