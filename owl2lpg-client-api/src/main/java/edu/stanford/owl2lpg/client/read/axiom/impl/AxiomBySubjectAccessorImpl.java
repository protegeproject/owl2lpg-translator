package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomBySubjectAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomBySubjectAccessorImpl implements AxiomBySubjectAccessor {

  private static final String CLASS_AXIOM_BY_SUBJECT_QUERY_FILE = "axioms/class-axiom-by-subject.cpy";
  private static final String NAMED_INDIVIDUAL_AXIOM_BY_SUBJECT_QUERY_FILE = "axioms/named-individual-axiom-by-subject.cpy";
  private static final String ANY_AXIOM_BY_SUBJECT_QUERY_FILE = "axioms/axiom-by-subject.cpy";

  private static final String CLASS_AXIOM_BY_SUBJECT_QUERY = read(CLASS_AXIOM_BY_SUBJECT_QUERY_FILE);
  private static final String NAMED_INDIVIDUAL_AXIOM_BY_SUBEJCT_QUERY = read(NAMED_INDIVIDUAL_AXIOM_BY_SUBJECT_QUERY_FILE);
  private static final String ANY_AXIOM_BY_SUBJECT_QUERY = read(ANY_AXIOM_BY_SUBJECT_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AxiomBySubjectAccessorImpl(@Nonnull GraphReader graphReader,
                                    @Nonnull NodeMapper nodeMapper) {
    this.graphReader = checkNotNull(graphReader);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAxiom>
  getAxiomsBySubject(@Nonnull OWLClass subject,
                     @Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId ontoDocId) {
    return getAxiomsBySubject(CLASS_AXIOM_BY_SUBJECT_QUERY, subject, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAxiom>
  getAxiomsBySubject(@Nonnull OWLNamedIndividual subject,
                     @Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId ontoDocId) {
    return getAxiomsBySubject(NAMED_INDIVIDUAL_AXIOM_BY_SUBEJCT_QUERY, subject, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAxiom>
  getAxiomsBySubject(@Nonnull OWLEntity subject,
                     @Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId ontoDocId) {
    return getAxiomsBySubject(ANY_AXIOM_BY_SUBJECT_QUERY, subject, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAxiom>
  getAxiomsBySubjects(@Nonnull Collection<OWLEntity> entities,
                      @Nonnull ProjectId projectId,
                      @Nonnull BranchId branchId,
                      @Nonnull OntologyDocumentId ontoDocId) {
    return entities.stream()
        .flatMap(entity -> getAxiomsBySubject(entity, projectId, branchId, ontoDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private ImmutableSet<OWLAxiom> getAxiomsBySubject(String queryString, OWLEntity subject,
                                                    ProjectId projectId,
                                                    BranchId branchId,
                                                    OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(subject, projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(queryString, inputParams);
    return collectAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLAxiom> collectAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private static Value createInputParams(OWLEntity entity, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(entity.getIRI(), projectId, branchId, ontoDocId);
  }
}
