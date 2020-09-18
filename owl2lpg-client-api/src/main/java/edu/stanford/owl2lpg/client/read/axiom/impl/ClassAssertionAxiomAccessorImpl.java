package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.ClassAssertionAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.AxiomType;
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

  private static final String CLASS_ASSERTION_AXIOMS_BY_TYPE_QUERY_FILE = "read/axioms/class-assertion-axioms-by-type.cpy";
  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE = "read/axioms/class-assertion-axiom-by-individual.cpy";
  private static final String CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE = "read/axioms/class-assertion-axiom-by-anonymous-individual.cpy";

  private static final String CLASS_ASSERTION_AXIOMS_BY_TYPE_QUERY = read(CLASS_ASSERTION_AXIOMS_BY_TYPE_QUERY_FILE);
  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY = read(CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY = read(CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public ClassAssertionAxiomAccessorImpl(@Nonnull GraphReader graphReader,
                                         @Nonnull NodeMapper nodeMapper,
                                         @Nonnull AxiomAccessor axiomAccessor) {
    this.graphReader = checkNotNull(graphReader);
    this.nodeMapper = checkNotNull(nodeMapper);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLClassAssertionAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                                           @Nonnull BranchId branchId,
                                                           @Nonnull OntologyDocumentId ontoDocId) {
    return axiomAccessor.getAxiomsByType(AxiomType.CLASS_ASSERTION, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLClassAssertionAxiom> getAxiomsByType(@Nonnull OWLClass owlClass,
                                                              @Nonnull ProjectId projectId,
                                                              @Nonnull BranchId branchId,
                                                              @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(owlClass, projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(CLASS_ASSERTION_AXIOMS_BY_TYPE_QUERY, inputParams);
    return collectClassAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLClassAssertionAxiom> getAxiomsBySubject(@Nonnull OWLIndividual owlIndividual,
                                                                 @Nonnull ProjectId projectId,
                                                                 @Nonnull BranchId branchId,
                                                                 @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        graphReader.getNodeIndex(CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLNamedIndividual().getIRI(), projectId, branchId, ontoDocId)) :
        graphReader.getNodeIndex(CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLAnonymousIndividual().getID(), projectId, branchId, ontoDocId));
    return collectClassAssertionAxiomsFromIndex(nodeIndex);
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
