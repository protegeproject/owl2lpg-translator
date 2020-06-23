package edu.stanford.owl2lpg.client.read.frame2;

import com.google.common.collect.Sets;
import edu.stanford.owl2lpg.client.read.frame.Parameters;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

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
  public Set<OWLAxiom> getAxioms(AxiomContext context, OWLClass subject) {
    var nodeIndex = getNodeIndex(context, subject);
    var axiomNodes = nodeIndex.getStartNodes();
    return axiomNodes.stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAxiom.class))
        .collect(Collectors.toSet());
  }

  private NodeIndex getNodeIndex(AxiomContext context, OWLClass subject) {
    var args = Parameters.forSubject(context, subject);
    var nodeIndex = session.readTransaction(tx -> {
      var result = tx.run(QUERY_STRING, args);
      var startNodes = Sets.<Node>newHashSet();
      var segments = Sets.<Path.Segment>newHashSet();
      while (result.hasNext()) {
        var row = result.next().asMap();
        for (var column : row.entrySet()) {
          if (column.getKey().equals("n")) {
            startNodes.add((Node) column.getValue());
          }
          if (column.getKey().equals("p")) {
            var path = (Path) column.getValue();
            path.iterator().forEachRemaining(segments::add);
          }
        }
      }
      var builder = new NodeIndex.Builder(startNodes);
      segments.forEach(builder::add);
      return builder.build();
    });
    return nodeIndex;
  }

  private final String QUERY_STRING =
      "MATCH (n:Axiom)-[:AXIOM_SUBJECT]->(c:Class {iri: $subjectIri})\n" +
          "MATCH p=(n)-[* {structuralSpec:true}]->()\n" +
          "RETURN n, p";
}
