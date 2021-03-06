package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.impl.EntityNodeMapper;
import edu.stanford.owl2lpg.client.read.hierarchy.ClassHierarchyAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassHierarchyAccessorImpl implements ClassHierarchyAccessor {

  private static final String CLASS_CHILDREN_OF_OWL_THING_QUERY_FILE = "read/hierarchy/class-children-of-owl-thing.cpy";
  private static final String CLASS_ANCESTOR_QUERY_FILE = "read/hierarchy/class-ancestor.cpy";
  private static final String CLASS_PARENTS_QUERY_FILE = "read/hierarchy/class-parents.cpy";
  private static final String CLASS_DESCENDANT_QUERY_FILE = "read/hierarchy/class-descendant.cpy";
  private static final String CLASS_CHILDREN_QUERY_FILE = "read/hierarchy/class-children.cpy";
  private static final String CLASS_PATHS_TO_ANCESTOR_QUERY_FILE = "read/hierarchy/class-paths-to-ancestor.cpy";

  private static final String CLASS_CHILDREN_OF_OWL_THING_QUERY = read(CLASS_CHILDREN_OF_OWL_THING_QUERY_FILE);
  private static final String CLASS_ANCESTOR_QUERY = read(CLASS_ANCESTOR_QUERY_FILE);
  private static final String CLASS_PARENTS_QUERY = read(CLASS_PARENTS_QUERY_FILE);
  private static final String CLASS_DESCENDANT_QUERY = read(CLASS_DESCENDANT_QUERY_FILE);
  private static final String CLASS_CHILDREN_QUERY = read(CLASS_CHILDREN_QUERY_FILE);
  private static final String PATHS_TO_ANCESTOR_QUERY = read(CLASS_PATHS_TO_ANCESTOR_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final EntityNodeMapper entityNodeMapper;

  @Nonnull
  private OWLClass root;

  @Inject
  public ClassHierarchyAccessorImpl(@Nonnull GraphReader graphReader,
                                    @Nonnull EntityNodeMapper entityNodeMapper,
                                    @Nonnull OWLDataFactory dataFactory) {
    this.graphReader = checkNotNull(graphReader);
    this.entityNodeMapper = checkNotNull(entityNodeMapper);
    this.root = dataFactory.getOWLThing();
  }

  @Override
  public void setRoot(@Nonnull OWLClass root) {
    this.root = root;
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLClass> getAncestors(@Nonnull OWLClass owlClass,
                                             @Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull OntologyDocumentId ontoDocId) {
    return getClasses(CLASS_ANCESTOR_QUERY, createInputParams(owlClass, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLClass> getDescendants(@Nonnull OWLClass owlClass,
                                               @Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull OntologyDocumentId ontoDocId) {
    return getClasses(CLASS_DESCENDANT_QUERY, createInputParams(owlClass, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLClass> getParents(@Nonnull OWLClass owlClass,
                                           @Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId,
                                           @Nonnull OntologyDocumentId ontoDocId) {
    return getClasses(CLASS_PARENTS_QUERY, createInputParams(owlClass, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLClass> getChildren(@Nonnull OWLClass owlClass,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId) {
    return getClasses(CLASS_CHILDREN_QUERY, createInputParams(owlClass, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLClass> getTopChildren(@Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull OntologyDocumentId ontoDocId) {
    return getClasses(CLASS_CHILDREN_OF_OWL_THING_QUERY, createInputParams(projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public Collection<List<OWLClass>> getPathsToRoot(@Nonnull OWLClass owlClass,
                                                   @Nonnull ProjectId projectId,
                                                   @Nonnull BranchId branchId,
                                                   @Nonnull OntologyDocumentId ontoDocId) {
    return getPathsToAncestor(owlClass, projectId, branchId, ontoDocId)
        .stream()
        .map(ClassAncestorPath::asOrderedList)
        .map(ImmutableList::reverse)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(@Nonnull OWLClass parent,
                            @Nonnull OWLClass child,
                            @Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId ontoDocId) {
    return getAncestors(child, projectId, branchId, ontoDocId).contains(parent);
  }

  @Override
  public boolean isLeaf(@Nonnull OWLClass owlClass,
                        @Nonnull ProjectId projectId,
                        @Nonnull BranchId branchId,
                        @Nonnull OntologyDocumentId ontoDocId) {
    return getChildren(owlClass, projectId, branchId, ontoDocId).size() == 0;
  }

  @Nonnull
  private ImmutableSet<OWLClass> getClasses(String queryString, Value inputParams) {
    return graphReader.getNodes(queryString, inputParams)
        .stream()
        .map(entityNodeMapper::toOwlClass)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private ImmutableList<ClassAncestorPath> getPathsToAncestor(OWLClass ancestor,
                                                              ProjectId projectId,
                                                              BranchId branchId,
                                                              OntologyDocumentId ontoDocId) {
    var ancestorPaths = ImmutableList.<ClassAncestorPath>builder();
    graphReader.getPaths(PATHS_TO_ANCESTOR_QUERY, createInputParams(ancestor, projectId, branchId, ontoDocId))
        .stream()
        .map(path -> Streams.stream(path.nodes())
            .map(entityNodeMapper::toOwlEntity)
            .map(OWLEntity::asOWLClass)
            .collect(ImmutableList.toImmutableList()))
        .map(this::insertRootToPathIfNecessary)
        .map(ClassAncestorPath::get)
        .forEach(ancestorPaths::add);
    return ancestorPaths.build();
  }

  private ImmutableList<OWLClass> insertRootToPathIfNecessary(ImmutableList<OWLClass> path) {
    var newPath = ImmutableList.<OWLClass>builder();
    newPath.addAll(path);
    var topClass = path.get(path.size() - 1);
    if (!topClass.equals(root)) {
      newPath.add(root);
    }
    return newPath.build();
  }


  @Nonnull
  private static Value createInputParams(OWLClass owlClass,
                                         ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(owlClass.getIRI(), projectId, branchId, ontoDocId);
  }

  @Nonnull
  private static Value createInputParams(ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forContext(projectId, branchId, ontoDocId);
  }
}
