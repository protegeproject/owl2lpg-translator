package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.neo4j.driver.Driver;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomByEntityAccessorImpl implements AxiomByEntityAccessor {

  private static final String SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY_FILE = "axioms/sub-class-of-axiom-by-sub-class.cpy";

  private static final String SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY = read(SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AxiomByEntityAccessorImpl(@Nonnull Driver driver,
                                   @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Override
  public Set<OWLAxiom> getSubClassOfAxiomsBySubClass(AxiomContext context, OWLClass subClass) {
    var nodeIndex = getNodeIndex(context, subClass, SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY);
    return collectAxiomsFromIndex(nodeIndex);
  }

  private NodeIndex getNodeIndex(AxiomContext context, OWLClass subClass, String queryString) {
    try (var session = driver.session()) {
      var args = Parameters.forEntity(context, subClass);
      return session.readTransaction(tx -> {
        var result = tx.run(queryString, args);
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

  @Nonnull
  private Set<OWLAxiom> collectAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAxiom.class))
        .collect(Collectors.toSet());
  }
}
