package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.IndividualsIndex;
import edu.stanford.bmir.protege.web.server.index.IndividualsQueryResult;
import edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jIndividualsIndex implements IndividualsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Nonnull
  private final AssertionAxiomAccessor assertionAxiomAccessor;

  @Nonnull
  private final Neo4jIndividualsByNameIndex individualsByNameIndex;

  @Nonnull
  private final Neo4jIndividualsBySubjectIndex individualsBySubjectIndex;

  @Inject
  public Neo4jIndividualsIndex(@Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull ProjectAccessor projectAccessor,
                               @Nonnull Neo4jIndividualsByNameIndex individualsByNameIndex,
                               @Nonnull Neo4jIndividualsBySubjectIndex individualsBySubjectIndex,
                               @Nonnull AssertionAxiomAccessor assertionAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.projectAccessor = checkNotNull(projectAccessor);
    this.individualsByNameIndex = checkNotNull(individualsByNameIndex);
    this.individualsBySubjectIndex = checkNotNull(individualsBySubjectIndex);
    this.assertionAxiomAccessor = checkNotNull(assertionAxiomAccessor);
  }

  @Nonnull
  @Override
  public IndividualsQueryResult getIndividuals(@Nonnull String searchString,
                                               @Nonnull PageRequest pageRequest) {
    return individualsByNameIndex.getIndividuals(searchString, pageRequest);
  }

  @Nonnull
  @Override
  public IndividualsQueryResult getIndividuals(@Nonnull OWLClass owlClass,
                                               @Nonnull InstanceRetrievalMode instanceRetrievalMode,
                                               @Nonnull String searchString,
                                               @Nonnull PageRequest pageRequest) {
    return individualsByNameIndex.getIndividuals(owlClass, instanceRetrievalMode, searchString, pageRequest);
  }

  @Nonnull
  @Override
  public IndividualsQueryResult getIndividualsPageContaining(@Nonnull OWLNamedIndividual individual,
                                                             @Nonnull Optional<OWLClass> owlClass,
                                                             @Nonnull InstanceRetrievalMode instanceRetrievalMode,
                                                             int pageSize) {
    return individualsBySubjectIndex.getIndividualsPageContaining(individual, owlClass, instanceRetrievalMode, pageSize);
  }

  @Nonnull
  @Override
  public Stream<OWLClass> getTypes(@Nonnull OWLNamedIndividual owlNamedIndividual) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(documentId -> assertionAxiomAccessor.getClassAssertionsBySubject(owlNamedIndividual, projectId, branchId, documentId).stream())
        .map(OWLClassAssertionAxiom::getClassExpression)
        .filter(OWLClassExpression::isNamed)
        .map(OWLClassExpression::asOWLClass);
  }
}
