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
import edu.stanford.owl2lpg.client.read.hierarchy.AnnotationPropertyHierarchyAccessor;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

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
public class AnnotationPropertyHierarchyAccessorImpl implements AnnotationPropertyHierarchyAccessor {

  private static final String ANNOTATION_PROPERTY_CHILDREN_OF_ROOT_QUERY_FILE = "read/hierarchy/annotation-property-children-of-root.cpy";
  private static final String ANNOTATION_PROPERTY_ANCESTOR_QUERY_FILE = "read/hierarchy/annotation-property-ancestor.cpy";
  private static final String ANNOTATION_PROPERTY_PARENTS_QUERY_FILE = "read/hierarchy/annotation-property-parents.cpy";
  private static final String ANNOTATION_PROPERTY_DESCENDANT_QUERY_FILE = "read/hierarchy/annotation-property-descendant.cpy";
  private static final String ANNOTATION_PROPERTY_CHILDREN_QUERY_FILE = "read/hierarchy/annotation-property-children.cpy";
  private static final String ANNOTATION_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE = "read/hierarchy/annotation-property-paths-to-ancestor.cpy";
  private static final String ANNOTATION_PROPERTY_COUNT_CHILDREN_QUERY_FILE = "read/hierarchy/annotation-property-count-children.cpy";

  private static final String PROPERTY_CHILDREN_OF_ROOT_QUERY = read(ANNOTATION_PROPERTY_CHILDREN_OF_ROOT_QUERY_FILE);
  private static final String PROPERTY_ANCESTOR_QUERY = read(ANNOTATION_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String PROPERTY_PARENTS_QUERY = read(ANNOTATION_PROPERTY_PARENTS_QUERY_FILE);
  private static final String PROPERTY_DESCENDANT_QUERY = read(ANNOTATION_PROPERTY_DESCENDANT_QUERY_FILE);
  private static final String PROPERTY_CHILDREN_QUERY = read(ANNOTATION_PROPERTY_CHILDREN_QUERY_FILE);
  private static final String PATHS_TO_ANCESTOR_QUERY = read(ANNOTATION_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE);
  private static final String ANNOTATION_PROPERTY_COUNT_CHILDREN_QUERY = read(ANNOTATION_PROPERTY_COUNT_CHILDREN_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final EntityNodeMapper entityNodeMapper;

  @Inject
  public AnnotationPropertyHierarchyAccessorImpl(@Nonnull GraphReader graphReader,
                                                 @Nonnull EntityNodeMapper entityNodeMapper) {
    this.graphReader = checkNotNull(graphReader);
    this.entityNodeMapper = checkNotNull(entityNodeMapper);
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLAnnotationProperty> getRoots(@Nonnull ProjectId projectId,
                                                      @Nonnull BranchId branchId,
                                                      @Nonnull OntologyDocumentId ontoDocId) {
    return getAnnotationProperties(PROPERTY_CHILDREN_OF_ROOT_QUERY, createInputParams(projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLAnnotationProperty> getAncestors(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                                          @Nonnull ProjectId projectId,
                                                          @Nonnull BranchId branchId,
                                                          @Nonnull OntologyDocumentId ontoDocId) {
    return getAnnotationProperties(PROPERTY_ANCESTOR_QUERY, createInputParams(owlAnnotationProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLAnnotationProperty> getDescendants(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                                            @Nonnull ProjectId projectId,
                                                            @Nonnull BranchId branchId,
                                                            @Nonnull OntologyDocumentId ontoDocId) {
    return getAnnotationProperties(PROPERTY_DESCENDANT_QUERY, createInputParams(owlAnnotationProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLAnnotationProperty> getParents(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                                        @Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull OntologyDocumentId ontoDocId) {
    return getAnnotationProperties(PROPERTY_PARENTS_QUERY, createInputParams(owlAnnotationProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLAnnotationProperty> getChildren(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                                         @Nonnull ProjectId projectId,
                                                         @Nonnull BranchId branchId,
                                                         @Nonnull OntologyDocumentId ontoDocId) {
    return getAnnotationProperties(PROPERTY_CHILDREN_QUERY, createInputParams(owlAnnotationProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public Collection<List<OWLAnnotationProperty>> getPathsToRoot(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                                                @Nonnull ProjectId projectId,
                                                                @Nonnull BranchId branchId,
                                                                @Nonnull OntologyDocumentId ontoDocId) {
    return getPathsToAncestor(owlAnnotationProperty, projectId, branchId, ontoDocId)
        .stream()
        .map(AnnotationPropertyAncestorPath::asOrderedList)
        .map(ImmutableList::reverse)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(@Nonnull OWLAnnotationProperty parent,
                            @Nonnull OWLAnnotationProperty child,
                            @Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId ontoDocId) {
    return getAncestors(child, projectId, branchId, ontoDocId).contains(parent);
  }

  @Override
  public boolean isLeaf(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                        @Nonnull ProjectId projectId,
                        @Nonnull BranchId branchId,
                        @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = Parameters.forEntity(owlAnnotationProperty, projectId, branchId, ontoDocId);
    var childrenCount = graphReader.getInteger(ANNOTATION_PROPERTY_COUNT_CHILDREN_QUERY, inputParams, "count");
    return childrenCount == 0;
  }

  @Nonnull
  private ImmutableSet<OWLAnnotationProperty> getAnnotationProperties(String queryString, Value inputParams) {
    return graphReader.getNodes(queryString, inputParams)
        .stream()
        .map(entityNodeMapper::toOwlAnnotationProperty)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private ImmutableList<AnnotationPropertyAncestorPath> getPathsToAncestor(OWLAnnotationProperty ancestor,
                                                                           ProjectId projectId,
                                                                           BranchId branchId,
                                                                           OntologyDocumentId ontoDocId) {
    var ancestorPaths = ImmutableList.<AnnotationPropertyAncestorPath>builder();
    graphReader.getPaths(PATHS_TO_ANCESTOR_QUERY, createInputParams(ancestor, projectId, branchId, ontoDocId))
        .stream()
        .map(path -> Streams.stream(path.nodes())
            .map(entityNodeMapper::toOwlAnnotationProperty)
            .collect(ImmutableList.toImmutableList()))
        .map(AnnotationPropertyAncestorPath::get)
        .forEach(ancestorPaths::add);
    return ancestorPaths.build();
  }

  @Nonnull
  private static Value createInputParams(OWLAnnotationProperty owlAnnotationProperty,
                                         ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(owlAnnotationProperty.getIRI(), projectId, branchId, ontoDocId);
  }

  @Nonnull
  private static Value createInputParams(ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forContext(projectId, branchId, ontoDocId);
  }
}
