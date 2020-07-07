package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.client.read.frame.Parameters;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.read.axiom.CypherQueries.AXIOM_SUBJECT_QUERY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomSubjectAccessorImpl implements AxiomSubjectAccessor {

  @Nonnull
  private final Session session;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AxiomSubjectAccessorImpl(@Nonnull Session session,
                                  @Nonnull NodeMapper nodeMapper) {
    this.session = checkNotNull(session);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Override
  public Set<OWLAxiom> getAxiomSubject(AxiomContext context, OWLClass subject) {
    var nodeIndex = getNodeIndex(context, subject);
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAxiom.class))
        .collect(Collectors.toSet());
  }

  private NodeIndex getNodeIndex(AxiomContext context, OWLClass subject) {
    var args = Parameters.forSubject(context, subject);
    return session.readTransaction(tx -> {
      var result = tx.run(AXIOM_SUBJECT_QUERY, args);
      var nodeIndexBuilder = new NodeIndexImpl.Builder();
      while (result.hasNext()) {
        var row = result.next().asMap();
        for (var column : row.entrySet()) {
          if (column.getKey().equals("p")) {
            var path = (Path) column.getValue();
            if (path != null) {
              path.spliterator().forEachRemaining(nodeIndexBuilder::add);
            }
          }
        }
      }
      return nodeIndexBuilder.build();
    });
  }
}
