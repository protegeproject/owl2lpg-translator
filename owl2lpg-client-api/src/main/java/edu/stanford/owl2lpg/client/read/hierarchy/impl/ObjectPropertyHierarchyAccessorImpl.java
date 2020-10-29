package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.impl.EntityNodeMapper;
import edu.stanford.owl2lpg.client.read.hierarchy.ObjectPropertyHierarchyAccessor;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

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
public class ObjectPropertyHierarchyAccessorImpl implements ObjectPropertyHierarchyAccessor {

  private static final String OBJECT_PROPERTY_CHILDREN_OF_OWL_TOP_OBJECT_PROPERTY_QUERY_FILE =
      "read/hierarchy/object-property-children-of-owl-top-object-property.cpy";
  private static final String OBJECT_PROPERTY_ANCESTOR_QUERY_FILE = "read/hierarchy/object-property-ancestor.cpy";
  private static final String OBJECT_PROPERTY_PARENTS_QUERY_FILE = "read/hierarchy/object-property-parents.cpy";
  private static final String OBJECT_PROPERTY_DESCENDANT_QUERY_FILE = "read/hierarchy/object-property-descendant.cpy";
  private static final String OBJECT_PROPERTY_CHILDREN_QUERY_FILE = "read/hierarchy/object-property-children.cpy";
  private static final String OBJECT_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE = "read/hierarchy/object-property-paths-to-ancestor.cpy";

  private static final String OBJECT_PROPERTY_CHILDREN_OF_OWL_TOP_OBJECT_PROPERTY_QUERY =
      read(OBJECT_PROPERTY_CHILDREN_OF_OWL_TOP_OBJECT_PROPERTY_QUERY_FILE);
  private static final String OBJECT_PROPERTY_ANCESTOR_QUERY = read(OBJECT_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String OBJECT_PROPERTY_PARENTS_QUERY = read(OBJECT_PROPERTY_PARENTS_QUERY_FILE);
  private static final String OBJECT_PROPERTY_DESCENDANT_QUERY = read(OBJECT_PROPERTY_DESCENDANT_QUERY_FILE);
  private static final String OBJECT_PROPERTY_CHILDREN_QUERY = read(OBJECT_PROPERTY_CHILDREN_QUERY_FILE);
  private static final String PATHS_TO_ANCESTOR_QUERY = read(OBJECT_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final EntityNodeMapper entityNodeMapper;

  @Nonnull
  private OWLObjectProperty root;

  @Inject
  public ObjectPropertyHierarchyAccessorImpl(@Nonnull GraphReader graphReader,
                                             @Nonnull EntityNodeMapper entityNodeMapper,
                                             @Nonnull OWLDataFactory dataFactory) {
    this.graphReader = checkNotNull(graphReader);
    this.entityNodeMapper = checkNotNull(entityNodeMapper);
    this.root = dataFactory.getOWLTopObjectProperty();
  }

  @Override
  public void setRoot(OWLObjectProperty root) {
    this.root = root;
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLObjectProperty> getAncestors(@Nonnull OWLObjectProperty owlObjectProperty,
                                                      @Nonnull ProjectId projectId,
                                                      @Nonnull BranchId branchId,
                                                      @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(OBJECT_PROPERTY_ANCESTOR_QUERY, createInputParams(owlObjectProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLObjectProperty> getDescendants(@Nonnull OWLObjectProperty owlObjectProperty,
                                                        @Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(OBJECT_PROPERTY_DESCENDANT_QUERY, createInputParams(owlObjectProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLObjectProperty> getParents(@Nonnull OWLObjectProperty owlObjectProperty,
                                                    @Nonnull ProjectId projectId,
                                                    @Nonnull BranchId branchId,
                                                    @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(OBJECT_PROPERTY_PARENTS_QUERY, createInputParams(owlObjectProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLObjectProperty> getChildren(@Nonnull OWLObjectProperty owlObjectProperty,
                                                     @Nonnull ProjectId projectId,
                                                     @Nonnull BranchId branchId,
                                                     @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(OBJECT_PROPERTY_CHILDREN_QUERY, createInputParams(owlObjectProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLObjectProperty> getTopChildren(@Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(OBJECT_PROPERTY_CHILDREN_OF_OWL_TOP_OBJECT_PROPERTY_QUERY, createInputParams(projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public Collection<List<OWLObjectProperty>> getPathsToRoot(@Nonnull OWLObjectProperty owlObjectProperty,
                                                            @Nonnull ProjectId projectId,
                                                            @Nonnull BranchId branchId,
                                                            @Nonnull OntologyDocumentId ontoDocId) {
    return getPathsToAncestor(owlObjectProperty, projectId, branchId, ontoDocId)
        .stream()
        .map(ObjectPropertyAncestorPath::asOrderedList)
        .map(ImmutableList::reverse)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(@Nonnull OWLObjectProperty parent,
                            @Nonnull OWLObjectProperty child,
                            @Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId ontoDocId) {
    return getAncestors(child, projectId, branchId, ontoDocId).contains(parent);
  }

  @Override
  public boolean isLeaf(@Nonnull OWLObjectProperty owlObjectProperty,
                        @Nonnull ProjectId projectId,
                        @Nonnull BranchId branchId,
                        @Nonnull OntologyDocumentId ontoDocId) {
    return getChildren(owlObjectProperty, projectId, branchId, ontoDocId).size() == 0;
  }

  @Nonnull
  private ImmutableSet<OWLObjectProperty> getProperties(String queryString, Value inputParams) {
    return graphReader.getNodes(queryString, inputParams)
        .stream()
        .map(entityNodeMapper::toOwlObjectProperty)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private ImmutableList<ObjectPropertyAncestorPath> getPathsToAncestor(OWLObjectProperty ancestor,
                                                                       ProjectId projectId,
                                                                       BranchId branchId,
                                                                       OntologyDocumentId ontoDocId) {
    var ancestorPaths = ImmutableList.<ObjectPropertyAncestorPath>builder();
    graphReader.getPaths(PATHS_TO_ANCESTOR_QUERY, createInputParams(ancestor, projectId, branchId, ontoDocId))
        .stream()
        .map(path -> Streams.stream(path.nodes())
            .map(entityNodeMapper::toOwlObjectProperty)
            .collect(ImmutableList.toImmutableList()))
        .map(this::insertRootToPathIfNecessary)
        .map(ObjectPropertyAncestorPath::get)
        .forEach(ancestorPaths::add);
    return ancestorPaths.build();
  }

  private ImmutableList<OWLObjectProperty> insertRootToPathIfNecessary(ImmutableList<OWLObjectProperty> path) {
    var newPath = ImmutableList.<OWLObjectProperty>builder();
    newPath.addAll(path);
    var topObjectProperty = path.get(path.size() - 1);
    if (!topObjectProperty.equals(root)) {
      newPath.add(root);
    }
    return newPath.build();
  }

  @Nonnull
  private static Value createInputParams(OWLObjectProperty owlObjectProperty,
                                         ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(owlObjectProperty.getIRI(), projectId, branchId, ontoDocId);
  }

  @Nonnull
  private static Value createInputParams(ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forContext(projectId, branchId, ontoDocId);
  }
}
