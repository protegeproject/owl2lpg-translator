package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AnnotationAssertionAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_ASSERTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationAssertionAxiomAccessorImpl implements AnnotationAssertionAxiomAccessor {

  private static final String ANNOTATION_ASSERTION_AXIOM_BY_IRI_QUERY_FILE =
      "axioms/annotation-assertion-axiom-by-iri.cpy";
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/annotation-assertion-axiom-by-anonymous-individual.cpy";
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_VALUE_LITERAL_QUERY_FILE =
      "axioms/annotation-assertion-axiom-by-value-literal.cpy";
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_VALUE_IRI_QUERY_FILE =
      "axioms/annotation-assertion-axiom-by-value-iri.cpy";
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_VALUE_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "axioms/annotation-assertion-axiom-by-value-anonymous-individual.cpy";

  private static final String ANNOTATION_ASSERTION_AXIOM_BY_IRI_QUERY =
      read(ANNOTATION_ASSERTION_AXIOM_BY_IRI_QUERY_FILE);
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(ANNOTATION_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_VALUE_LITERAL_QUERY =
      read(ANNOTATION_ASSERTION_AXIOM_BY_VALUE_LITERAL_QUERY_FILE);
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_VALUE_IRI_QUERY =
      read(ANNOTATION_ASSERTION_AXIOM_BY_VALUE_IRI_QUERY_FILE);
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_VALUE_ANONYMOUS_INDIVIDUAL_QUERY =
      read(ANNOTATION_ASSERTION_AXIOM_BY_VALUE_ANONYMOUS_INDIVIDUAL_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AnnotationAssertionAxiomAccessorImpl(@Nonnull GraphReader graphReader,
                                              @Nonnull NodeMapper nodeMapper) {
    this.graphReader = checkNotNull(graphReader);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationAssertionAxiom> getAxiomsBySubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                                                      @Nonnull ProjectId projectId,
                                                                      @Nonnull BranchId branchId,
                                                                      @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = getNodeIndex(owlAnnotationSubject, projectId, branchId, ontoDocId);
    return collectAnnotationAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private NodeIndex getNodeIndex(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                 @Nonnull ProjectId projectId,
                                 @Nonnull BranchId branchId,
                                 @Nonnull OntologyDocumentId ontoDocId) {
    if (owlAnnotationSubject.isIRI()) {
      return graphReader.getNodeIndex(
          ANNOTATION_ASSERTION_AXIOM_BY_IRI_QUERY,
          Parameters.forEntityIri((IRI) owlAnnotationSubject, projectId, branchId, ontoDocId));
    } else if (owlAnnotationSubject.isAnonymous()) {
      return graphReader.getNodeIndex(
          ANNOTATION_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
          Parameters.forNodeId(((OWLAnonymousIndividual) owlAnnotationSubject).getID(), projectId, branchId, ontoDocId));
    } else {
      throw new RuntimeException("Unknown type of OWL annotation value");
    }
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationAssertionAxiom> getAxiomsBySubjectAndProperty(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                                                                 @Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                                                                 @Nonnull ProjectId projectId,
                                                                                 @Nonnull BranchId branchId,
                                                                                 @Nonnull OntologyDocumentId ontoDocId) {
    return getAxiomsBySubject(owlAnnotationSubject, projectId, branchId, ontoDocId)
        .stream()
        .filter(ax -> ax.getProperty().equals(owlAnnotationProperty))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationAssertionAxiom> getAxiomsByValue(@Nonnull OWLAnnotationValue owlAnnotationValue,
                                                                    @Nonnull ProjectId projectId,
                                                                    @Nonnull BranchId branchId,
                                                                    @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = getNodeIndex(owlAnnotationValue, projectId, branchId, ontoDocId);
    return collectAnnotationAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private NodeIndex getNodeIndex(@Nonnull OWLAnnotationValue owlAnnotationValue,
                                 @Nonnull ProjectId projectId,
                                 @Nonnull BranchId branchId,
                                 @Nonnull OntologyDocumentId ontoDocId) {
    if (owlAnnotationValue.isLiteral()) {
      return graphReader.getNodeIndex(
          ANNOTATION_ASSERTION_AXIOM_BY_VALUE_LITERAL_QUERY,
          Parameters.forLiteral((OWLLiteral) owlAnnotationValue, projectId, branchId, ontoDocId));
    } else if (owlAnnotationValue.isIRI()) {
      return graphReader.getNodeIndex(
          ANNOTATION_ASSERTION_AXIOM_BY_VALUE_IRI_QUERY,
          Parameters.forValueIri((IRI) owlAnnotationValue, projectId, branchId, ontoDocId));
    } else if (owlAnnotationValue.isAnonymous()) {
      return graphReader.getNodeIndex(
          ANNOTATION_ASSERTION_AXIOM_BY_VALUE_ANONYMOUS_INDIVIDUAL_QUERY,
          Parameters.forNodeId(((OWLAnonymousIndividual) owlAnnotationValue).getID(), projectId, branchId, ontoDocId));
    }
    throw new RuntimeException("Unknown type of OWL annotation value");
  }

  @Nonnull
  private ImmutableSet<OWLAnnotationAssertionAxiom> collectAnnotationAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(ANNOTATION_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotationAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }
}
