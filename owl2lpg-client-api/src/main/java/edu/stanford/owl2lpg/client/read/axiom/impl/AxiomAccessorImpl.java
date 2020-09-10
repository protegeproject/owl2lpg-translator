package edu.stanford.owl2lpg.client.read.axiom.impl;

import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.impl.NodeIndexImpl;
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
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomAccessorImpl implements AxiomAccessor {

  private static final String ALL_AXIOM_QUERY_FILE = "axioms/all-axioms.cpy";
  private static final String AXIOM_BY_TYPE_QUERY_FILE = "axioms/axiom-by-type.cpy";

  private static final String ALL_AXIOM_QUERY = read(ALL_AXIOM_QUERY_FILE);
  private static final String AXIOM_BY_TYPE_QUERY = read(AXIOM_BY_TYPE_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AxiomAccessorImpl(@Nonnull Driver driver,
                           @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = Parameters.forContext(projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(ALL_AXIOM_QUERY, inputParams);
    return collectAxiomsFromNodeIndex(nodeIndex);
  }

  @Nonnull
  private Set<OWLAxiom> collectAxiomsFromNodeIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public <T extends OWLAxiom> Set<T> getAxiomsByType(@Nonnull AxiomType<T> axiomType,
                                                     @Nonnull ProjectId projectId,
                                                     @Nonnull BranchId branchId,
                                                     @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = Parameters.forAxiomType(axiomType, projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(AXIOM_BY_TYPE_QUERY, inputParams);
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
}
