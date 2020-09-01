package edu.stanford.owl2lpg.client.read.axiom.impl;

import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomByTypeAccessor;
import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomByTypeAccessorImpl implements AxiomByTypeAccessor {

  private static final String AXIOM_BY_TYPE_QUERY_FILE = "axioms/axiom-by-type.cpy";

  private static final String AXIOM_BY_TYPE_QUERY = read(AXIOM_BY_TYPE_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AxiomByTypeAccessorImpl(@Nonnull Driver driver,
                                 @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public <T extends OWLAxiom> Set<T> getAxiomsByType(AxiomType<T> axiomType, ProjectId projectId,
                                                     BranchId branchId, OntologyDocumentId ontoDocId) {
    var nodeIndex = getNodeIndex(AXIOM_BY_TYPE_QUERY, createInputParams(axiomType, projectId, branchId, ontoDocId));
    return collectAxiomsFromIndex(nodeIndex, axiomType);
  }

  @Nonnull
  private NodeIndex getNodeIndex(String queryString, Value inputParams) {
    try (var session = driver.session()) {
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
  private <T extends OWLAxiom> Set<T> collectAxiomsFromIndex(NodeIndex nodeIndex, AxiomType<T> axiomType) {
    return nodeIndex.getNodes(axiomType.getName())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, axiomType.getActualClass()))
        .collect(Collectors.toSet());
  }

  @Nonnull
  private static <T extends OWLAxiom> Value createInputParams(AxiomType<T> axiomType, ProjectId projectId,
                                                              BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forAxiomType(axiomType, projectId, branchId, ontoDocId);
  }
}
