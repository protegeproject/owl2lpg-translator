package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AnnotationAssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.axiom.ClassAssertionAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Value;
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

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_ASSERTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AssertionAxiomBySubjectAccessorImpl implements AssertionAxiomBySubjectAccessor {

  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/object-property-assertion-axiom-by-individual.cpy";
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/object-property-assertion-axiom-by-anonymous-individual.cpy";
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/data-property-assertion-axiom-by-individual.cpy";
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/data-property-assertion-axiom-by-anonymous-individual.cpy";

  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Nonnull
  private final ClassAssertionAxiomAccessor classAssertionAxiomAccessor;

  @Nonnull
  private final AnnotationAssertionAxiomAccessor annotationAssertionAxiomAccessor;

  @Inject
  public AssertionAxiomBySubjectAccessorImpl(@Nonnull GraphReader graphReader,
                                             @Nonnull NodeMapper nodeMapper,
                                             @Nonnull ClassAssertionAxiomAccessor classAssertionAxiomAccessor,
                                             @Nonnull AnnotationAssertionAxiomAccessor annotationAssertionAxiomAccessor) {
    this.graphReader = checkNotNull(graphReader);
    this.nodeMapper = checkNotNull(nodeMapper);
    this.classAssertionAxiomAccessor = checkNotNull(classAssertionAxiomAccessor);
    this.annotationAssertionAxiomAccessor = checkNotNull(annotationAssertionAxiomAccessor);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLClassAssertionAxiom>
  getClassAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                              @Nonnull ProjectId projectId,
                              @Nonnull BranchId branchId,
                              @Nonnull OntologyDocumentId ontoDocId) {
    return classAssertionAxiomAccessor.getAxiomsBySubject(owlIndividual, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLObjectPropertyAssertionAxiom>
  getObjectPropertyAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                                       @Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId,
                                       @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        graphReader.getNodeIndex(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLNamedIndividual().getIRI(), projectId, branchId, ontoDocId)) :
        graphReader.getNodeIndex(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLAnonymousIndividual().getID(), projectId, branchId, ontoDocId));
    return collectObjectPropertyAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLDataPropertyAssertionAxiom>
  getDataPropertyAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        graphReader.getNodeIndex(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLNamedIndividual().getIRI(), projectId, branchId, ontoDocId)) :
        graphReader.getNodeIndex(DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            createInputParams(owlIndividual.asOWLAnonymousIndividual().getID(), projectId, branchId, ontoDocId));
    return collectDataPropertyAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsBySubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontoDocId) {
    return annotationAssertionAxiomAccessor.getAxiomsBySubject(owlAnnotationSubject, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsBySubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                   @Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontoDocId) {
    return annotationAssertionAxiomAccessor.getAxiomsBySubjectAndProperty(owlAnnotationSubject, owlAnnotationProperty,
        projectId, branchId, ontoDocId);
  }

  @Nonnull
  private ImmutableSet<OWLObjectPropertyAssertionAxiom> collectObjectPropertyAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(OBJECT_PROPERTY_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLObjectPropertyAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private ImmutableSet<OWLDataPropertyAssertionAxiom> collectDataPropertyAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
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
