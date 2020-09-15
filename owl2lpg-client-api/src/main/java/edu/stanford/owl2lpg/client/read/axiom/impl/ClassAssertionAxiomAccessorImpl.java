package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.ClassAssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.impl.NodeIndexImpl;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS_ASSERTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassAssertionAxiomAccessorImpl implements ClassAssertionAxiomAccessor {

  private static final String CLASS_ASSERTION_AXIOMS_BY_TYPE_QUERY_FILE = "axioms/class-assertion-axioms-by-type.cpy";
  private static final String CLASS_ASSERTION_AXIOMS_OF_OWL_THING_QUERY_FILE = "axioms/class-assertion-axioms-of-owl-thing.cpy";
  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE = "axioms/class-assertion-axiom-by-individual.cpy";
  private static final String CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE = "axioms/class-assertion-axiom-by-anonymous-individual.cpy";

  private static final String CLASS_ASSERTION_AXIOMS_BY_TYPE_QUERY = read(CLASS_ASSERTION_AXIOMS_BY_TYPE_QUERY_FILE);
  private static final String CLASS_ASSERTION_AXIOMS_OF_OWL_THING_QUERY = read(CLASS_ASSERTION_AXIOMS_OF_OWL_THING_QUERY_FILE);
  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY = read(CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY = read(CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public ClassAssertionAxiomAccessorImpl(@Nonnull Driver driver,
                                         @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLClassAssertionAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                                           @Nonnull BranchId branchId,
                                                           @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(CLASS_ASSERTION_AXIOMS_OF_OWL_THING_QUERY, inputParams);
    return collectClassAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLClassAssertionAxiom> getAxiomsByType(@Nonnull OWLClass owlClass,
                                                              @Nonnull ProjectId projectId,
                                                              @Nonnull BranchId branchId,
                                                              @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(owlClass, projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(CLASS_ASSERTION_AXIOMS_BY_TYPE_QUERY, inputParams);
    return collectClassAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLClassAssertionAxiom> getAxiomsBySubject(@Nonnull OWLIndividual owlIndividual,
                                                                 @Nonnull ProjectId projectId,
                                                                 @Nonnull BranchId branchId,
                                                                 @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        getNodeIndex(CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLNamedIndividual().getIRI(), projectId, branchId, ontoDocId)) :
        getNodeIndex(CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLAnonymousIndividual().getID(), projectId, branchId, ontoDocId));
    return collectClassAssertionAxiomsFromIndex(nodeIndex);
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
  private ImmutableSet<OWLClassAssertionAxiom> collectClassAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(CLASS_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLClassAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private static Value createInputParams(ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forContext(projectId, branchId, ontoDocId);
  }

  @Nonnull
  private static Value createInputParams(OWLEntity entity, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(entity.getIRI(), projectId, branchId, ontoDocId);
  }

  @Nonnull
  private static Value createInputParams(IRI entityIri, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(entityIri, projectId, branchId, ontoDocId);
  }

  @Nonnull
  private static Value createInputParams(NodeID nodeId, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forNodeId(nodeId, projectId, branchId, ontoDocId);
  }
}
