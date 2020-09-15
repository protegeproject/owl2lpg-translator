package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AnnotationAssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.impl.NodeIndexImpl;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_ASSERTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AssertionAxiomBySubjectAccessorImpl implements AssertionAxiomBySubjectAccessor {

  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/class-assertion-axiom-by-individual.cpy";
  private static final String CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/class-assertion-axiom-by-anonymous-individual.cpy";
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/object-property-assertion-axiom-by-individual.cpy";
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/object-property-assertion-axiom-by-anonymous-individual.cpy";
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/data-property-assertion-axiom-by-individual.cpy";
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/data-property-assertion-axiom-by-anonymous-individual.cpy";

  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(CLASS_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Nonnull
  private final AnnotationAssertionAxiomAccessor annotationAssertionAxiomAccessor;

  @Inject
  public AssertionAxiomBySubjectAccessorImpl(@Nonnull Driver driver,
                                             @Nonnull NodeMapper nodeMapper,
                                             @Nonnull AnnotationAssertionAxiomAccessor annotationAssertionAxiomAccessor) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
    this.annotationAssertionAxiomAccessor = checkNotNull(annotationAssertionAxiomAccessor);
  }

  @Nonnull
  @Override
  public Set<OWLClassAssertionAxiom>
  getClassAssertionsForSubject(@Nonnull OWLIndividual owlIndividual,
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
  @Override
  public Set<OWLObjectPropertyAssertionAxiom>
  getObjectPropertyAssertionsForSubject(@Nonnull OWLIndividual owlIndividual,
                                        @Nonnull ProjectId projectId,
                                        @Nonnull BranchId branchId,
                                        @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        getNodeIndex(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLNamedIndividual().getIRI(), projectId, branchId, ontoDocId)) :
        getNodeIndex(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLAnonymousIndividual().getID(), projectId, branchId, ontoDocId));
    return collectObjectPropertyAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public Set<OWLDataPropertyAssertionAxiom>
  getDataPropertyAssertionsForSubject(@Nonnull OWLIndividual owlIndividual,
                                      @Nonnull ProjectId projectId,
                                      @Nonnull BranchId branchId,
                                      @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        getNodeIndex(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLNamedIndividual().getIRI(), projectId, branchId, ontoDocId)) :
        getNodeIndex(DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLAnonymousIndividual().getID(), projectId, branchId, ontoDocId));
    return collectDataPropertyAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public Set<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsForSubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId) {
    return annotationAssertionAxiomAccessor.getAxiomsBySubject(owlAnnotationSubject, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public Set<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsForSubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                    @Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId) {
    return annotationAssertionAxiomAccessor.getAxiomsBySubjectAndProperty(owlAnnotationSubject, owlAnnotationProperty,
        projectId, branchId, ontoDocId);
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
  private Set<OWLClassAssertionAxiom> collectClassAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(CLASS_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLClassAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private Set<OWLObjectPropertyAssertionAxiom> collectObjectPropertyAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(OBJECT_PROPERTY_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLObjectPropertyAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private Set<OWLDataPropertyAssertionAxiom> collectDataPropertyAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(DATA_PROPERTY_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLDataPropertyAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
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
