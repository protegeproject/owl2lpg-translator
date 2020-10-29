package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyRoot;
import edu.stanford.bmir.protege.web.server.index.IndividualsByTypeIndex;
import edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.hierarchy.ClassHierarchyAccessor;
import edu.stanford.owl2lpg.client.read.individual.NamedIndividualAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.DataFactory.getOWLThing;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jIndividualsByTypeIndex implements IndividualsByTypeIndex {

  @Nonnull
  private final OWLClass root;

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Nonnull
  private final ClassHierarchyAccessor classHierarchyAccessor;

  @Nonnull
  private final NamedIndividualAccessor namedIndividualAccessor;

  @Inject
  public Neo4jIndividualsByTypeIndex(@Nonnull @ClassHierarchyRoot OWLClass root,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull ProjectAccessor projectAccessor,
                                     @Nonnull ClassHierarchyAccessor classHierarchyAccessor,
                                     @Nonnull NamedIndividualAccessor namedIndividualAccessor) {
    this.root = checkNotNull(root);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.projectAccessor = checkNotNull(projectAccessor);
    this.classHierarchyAccessor = checkNotNull(classHierarchyAccessor);
    this.namedIndividualAccessor = checkNotNull(namedIndividualAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLNamedIndividual> getIndividualsByType(@Nonnull OWLClass owlClass,
                                                         @Nonnull InstanceRetrievalMode instanceRetrievalMode) {
    switch (instanceRetrievalMode) {
      case ALL_INSTANCES:
        return getAllInstances(owlClass);
      case DIRECT_INSTANCES:
        return getDirectInstances(owlClass);
      default:
        return Stream.empty();
    }
  }

  private Stream<OWLNamedIndividual> getAllInstances() {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(documentId -> namedIndividualAccessor.getAllIndividuals(projectId, branchId, documentId).stream());
  }

  private Stream<OWLNamedIndividual> getAllInstances(OWLClass owlClass) {
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return getAllInstances();
    } else {
      return Stream.concat(getDirectInstances(owlClass), getIndirectInstances(owlClass)).distinct();
    }
  }

  @Nonnull
  private Stream<OWLNamedIndividual> getDirectInstances(OWLClass owlClass) {
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return getAllInstances();
    } else {
      return projectAccessor.getOntologyDocumentIds(projectId, branchId)
          .stream()
          .flatMap(documentId -> namedIndividualAccessor.getIndividualsByType(owlClass, projectId, branchId, documentId).stream());
    }
  }

  @Nonnull
  private Stream<OWLNamedIndividual> getIndirectInstances(OWLClass owlClass) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(documentId -> classHierarchyAccessor.getDescendants(owlClass, projectId, branchId, documentId).stream())
        .flatMap(this::getDirectInstances)
        .distinct();
  }
}
