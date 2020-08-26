package edu.stanford.owl2lpg.client.read.axiom.impl;

import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.ClassAssertionAxiomByTypeAccessor;
import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS_ASSERTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassAssertionAxiomByTypeAccessorImpl implements ClassAssertionAxiomByTypeAccessor {

  private static final String CLASS_ASSERTION_AXIOM_BY_TYPE_QUERY_FILE = "axioms/class-assertion-axiom-by-type.cpy";

  private static final String CLASS_ASSERTION_AXIOM_BY_TYPE_QUERY = read(CLASS_ASSERTION_AXIOM_BY_TYPE_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public ClassAssertionAxiomByTypeAccessorImpl(@Nonnull Driver driver,
                                               @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLClassAssertionAxiom> getClassAssertionsForType(OWLClass owlClass, AxiomContext context) {
    var nodeIndex = getNodeIndex(CLASS_ASSERTION_AXIOM_BY_TYPE_QUERY, owlClass, context);
    return collectClassAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private NodeIndex getNodeIndex(String queryString, OWLEntity subject, AxiomContext context) {
    try (var session = driver.session()) {
      var inputParams = createInputParams(subject, context);
      return session.readTransaction(tx -> {
        var result = tx.run(queryString, inputParams);
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
  private Set<OWLClassAssertionAxiom> collectClassAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(CLASS_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLClassAssertionAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  private static Value createInputParams(OWLEntity entity, AxiomContext context) {
    return Parameters.forEntityIri(entity.getIRI(), context.getProjectId(), context.getBranchId(), context.getOntologyDocumentId());
  }
}
