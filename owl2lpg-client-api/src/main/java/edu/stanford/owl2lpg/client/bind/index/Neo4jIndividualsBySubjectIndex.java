package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.index.IndividualsQueryResult;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.hierarchy.ClassHierarchyAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.jetbrains.annotations.NotNull;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode.ALL_INSTANCES;
import static edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode.DIRECT_INSTANCES;
import static java.util.stream.Collectors.toList;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jIndividualsBySubjectIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Nonnull
  private final AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor;

  @Nonnull
  private final ClassHierarchyAccessor classHierarchyAccessor;

  @Nonnull
  private final Neo4jIndividualsByTypeIndex individualsByTypeIndex;

  @Inject
  public Neo4jIndividualsBySubjectIndex(@Nonnull ProjectId projectId,
                                        @Nonnull BranchId branchId,
                                        @Nonnull OntologyDocumentId ontoDocId,
                                        @Nonnull ProjectAccessor projectAccessor,
                                        @Nonnull AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor,
                                        @Nonnull ClassHierarchyAccessor classHierarchyAccessor,
                                        @Nonnull Neo4jIndividualsByTypeIndex individualsByTypeIndex) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.projectAccessor = checkNotNull(projectAccessor);
    this.assertionAxiomBySubjectAccessor = checkNotNull(assertionAxiomBySubjectAccessor);
    this.classHierarchyAccessor = checkNotNull(classHierarchyAccessor);
    this.individualsByTypeIndex = checkNotNull(individualsByTypeIndex);
  }

  @Nonnull
  public IndividualsQueryResult getIndividualsPageContaining(@Nonnull OWLNamedIndividual individual,
                                                             @Nonnull Optional<OWLClass> preferredType,
                                                             @Nonnull InstanceRetrievalMode preferredMode,
                                                             int pageSize) {
    OWLClass actualType = null;
    var matchingDirectType = Optional.<OWLClass>empty();
    var matchingIndirectType = Optional.<OWLClass>empty();
    if (preferredType.isPresent()) {
      var thePreferredType = preferredType.get();
      if (!thePreferredType.isOWLThing()) {
        var types = projectAccessor.getOntologyDocumentIds(projectId, branchId)
            .stream()
            .map(ontoDocId -> AxiomContext.create(projectId, branchId, ontoDocId))
            .flatMap(axiomContext -> assertionAxiomBySubjectAccessor.getClassAssertionsForSubject(individual, axiomContext).stream())
            .map(OWLClassAssertionAxiom::getClassExpression)
            .filter(OWLClassExpression::isNamed)
            .map(OWLClassExpression::asOWLClass)
            .collect(ImmutableSet.toImmutableSet());
        for (OWLClass type : types) {
          if (type.equals(thePreferredType)) {
            matchingDirectType = Optional.of(type);
          } else if (classHierarchyAccessor.getAncestors(thePreferredType, projectId, branchId, ontoDocId).contains(type)) {
            matchingIndirectType = Optional.of(type);
          }
        }
      }
      if (matchingDirectType.isPresent()) {
        actualType = matchingDirectType.get();
      } else if (matchingIndirectType.isPresent()) {
        actualType = matchingIndirectType.get();
      }
    }
    if (actualType == null) {
      // No preferred type or preferred type not found. Try for a specific type
      actualType = projectAccessor.getOntologyDocumentIds(projectId, branchId)
          .stream()
          .flatMap(ontoDoId -> assertionAxiomBySubjectAccessor.getClassAssertionsForSubject(
              individual,
              AxiomContext.create(projectId, branchId, ontoDocId)).stream())
          .map(OWLClassAssertionAxiom::getClassExpression)
          .filter(OWLClassExpression::isNamed)
          .map(OWLClassExpression::asOWLClass)
          .findFirst()
          .orElse(DataFactory.getOWLThing());
    }

    InstanceRetrievalMode actualMode = ALL_INSTANCES;
    if (preferredMode != ALL_INSTANCES && matchingDirectType.isPresent()) {
      actualMode = DIRECT_INSTANCES;
    }
    return getIndividualsQueryResult(individual, actualType, actualMode, pageSize);
  }

  @NotNull
  private IndividualsQueryResult getIndividualsQueryResult(@Nonnull OWLNamedIndividual individual, OWLClass
      actualType, InstanceRetrievalMode actualMode, int pageSize) {
    var individuals = individualsByTypeIndex.getIndividualsByType(actualType, actualMode).collect(toList());
    int individualIndex = individuals.indexOf(individual);
    var page = Page.<OWLNamedIndividual>emptyPage();
    if (individualIndex != -1) {
      int pageNumber = (individualIndex / pageSize) + 1;
      int pageCount = (individuals.size() / pageSize) + 1;
      int pageStartIndex = (individualIndex / pageSize) * pageSize;
      int pageEndIndex = Math.min(pageStartIndex + pageSize, individuals.size());
      page = new Page<>(pageNumber,
          pageCount,
          individuals.subList(pageStartIndex, pageEndIndex),
          individuals.size());
    }
    return IndividualsQueryResult.get(page, individuals.size(), actualType, actualMode);
  }
}
