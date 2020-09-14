package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyRoot;
import edu.stanford.bmir.protege.web.server.index.IndividualsByTypeIndex;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode;
import edu.stanford.owl2lpg.client.read.hierarchy.ClassHierarchyAccessor;
import edu.stanford.owl2lpg.client.read.individual.NamedIndividualAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

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
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final ClassHierarchyAccessor classHierarchyAccessor;

  @Nonnull
  private final NamedIndividualAccessor namedIndividualAccessor;

  @Inject
  public Neo4jIndividualsByTypeIndex(@Nonnull @ClassHierarchyRoot OWLClass root,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId,
                                     @Nonnull ClassHierarchyAccessor classHierarchyAccessor,
                                     @Nonnull NamedIndividualAccessor namedIndividualAccessor) {
    this.root = checkNotNull(root);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
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

  private Stream<OWLNamedIndividual> getAllInstances(OWLClass owlClass) {
    if (root.equals(DataFactory.getOWLThing()) && root.equals(owlClass)) {
      return namedIndividualAccessor.getAllIndividuals(projectId, branchId, ontoDocId).stream();
    } else {
      return Stream.concat(getDirectInstances(owlClass), getIndirectInstances(owlClass)).distinct();
    }
  }

  @Nonnull
  private Stream<OWLNamedIndividual> getDirectInstances(OWLClass owlClass) {
    return namedIndividualAccessor.getIndividualsByType(owlClass, projectId, branchId, ontoDocId).stream();
  }

  @Nonnull
  private Stream<OWLNamedIndividual> getIndirectInstances(OWLClass owlClass) {
    return classHierarchyAccessor.getDescendants(owlClass, projectId, branchId, ontoDocId)
        .stream()
        .flatMap(this::getDirectInstances);
  }
}
