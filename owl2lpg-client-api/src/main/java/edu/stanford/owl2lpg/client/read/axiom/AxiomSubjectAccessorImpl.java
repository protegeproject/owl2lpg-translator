package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
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
import static edu.stanford.owl2lpg.client.read.axiom.AxiomQueries.AXIOM_SUBJECT_QUERY;
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

  private final Table<AxiomContext, OWLClass, NodeIndex> nodeIndexCache = HashBasedTable.create();

  @Inject
  public AxiomSubjectAccessorImpl(@Nonnull Session session,
                                  @Nonnull NodeMapper nodeMapper) {
    this.session = checkNotNull(session);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Override
  public Set<OWLAxiom> getAxioms(AxiomContext context, OWLClass subject) {
    var nodeIndex = getNodeIndex(context, subject);
    var axiomNodes = nodeIndex.getNodes(AXIOM.getMainLabel());
    return axiomNodes.stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAxiom.class))
        .collect(Collectors.toSet());
  }

  private NodeIndex getNodeIndex(AxiomContext context, OWLClass subject) {
    var nodeIndex = nodeIndexCache.get(context, subject);
    if (nodeIndex == null) {
      nodeIndex = buildNodeIndex(context, subject);
      nodeIndexCache.put(context, subject, nodeIndex);
    }
    return nodeIndex;
  }

  private NodeIndex buildNodeIndex(AxiomContext context, OWLClass subject) {
    var args = Parameters.forSubject(context, subject);
    return session.readTransaction(tx -> {
      var result = tx.run(AXIOM_SUBJECT_QUERY, args);
      var segments = Sets.<Path.Segment>newHashSet();
      while (result.hasNext()) {
        var row = result.next().asMap();
        for (var column : row.entrySet()) {
          if (column.getKey().equals("p")) {
            var path = (Path) column.getValue();
            path.iterator().forEachRemaining(segments::add);
          }
        }
      }
      var builder = new NodeIndexImpl.Builder();
      segments.forEach(builder::add);
      return builder.build();
    });
  }
}
