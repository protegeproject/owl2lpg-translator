package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.hierarchy.DataPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

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
public class DataPropertyHierarchyAccessorImpl implements DataPropertyHierarchyAccessor {

  private static final String DATA_PROPERTY_CHILDREN_OF_OWL_TOP_DATA_PROPERTY_QUERY_FILE =
      "read/hierarchy/data-property-children-of-owl-top-data-property.cpy";
  private static final String DATA_PROPERTY_ANCESTOR_QUERY_FILE = "read/hierarchy/data-property-ancestor.cpy";
  private static final String DATA_PROPERTY_PARENTS_QUERY_FILE = "read/hierarchy/data-property-parents.cpy";
  private static final String DATA_PROPERTY_DESCENDANT_QUERY_FILE = "read/hierarchy/data-property-descendant.cpy";
  private static final String DATA_PROPERTY_CHILDREN_QUERY_FILE = "read/hierarchy/data-property-children.cpy";
  private static final String DATA_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE = "read/hierarchy/data-property-paths-to-ancestor.cpy";

  private static final String DATA_PROPERTY_CHILDREN_OF_OWL_TOP_DATA_PROPERTY_QUERY =
      read(DATA_PROPERTY_CHILDREN_OF_OWL_TOP_DATA_PROPERTY_QUERY_FILE);
  private static final String DATA_PROPERTY_ANCESTOR_QUERY = read(DATA_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String DATA_PROPERTY_PARENTS_QUERY = read(DATA_PROPERTY_PARENTS_QUERY_FILE);
  private static final String DATA_PROPERTY_DESCENDANT_QUERY = read(DATA_PROPERTY_DESCENDANT_QUERY_FILE);
  private static final String DATA_PROPERTY_CHILDREN_QUERY = read(DATA_PROPERTY_CHILDREN_QUERY_FILE);
  private static final String PATHS_TO_ANCESTOR_QUERY = read(DATA_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public DataPropertyHierarchyAccessorImpl(@Nonnull GraphReader graphReader,
                                           @Nonnull OWLDataFactory dataFactory) {
    this.graphReader = checkNotNull(graphReader);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLDataProperty> getAncestors(@Nonnull OWLDataProperty owlDataProperty,
                                                    @Nonnull ProjectId projectId,
                                                    @Nonnull BranchId branchId,
                                                    @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(DATA_PROPERTY_ANCESTOR_QUERY, createInputParams(owlDataProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLDataProperty> getDescendants(@Nonnull OWLDataProperty owlDataProperty,
                                                      @Nonnull ProjectId projectId,
                                                      @Nonnull BranchId branchId,
                                                      @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(DATA_PROPERTY_DESCENDANT_QUERY, createInputParams(owlDataProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLDataProperty> getParents(@Nonnull OWLDataProperty owlDataProperty,
                                                  @Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(DATA_PROPERTY_PARENTS_QUERY, createInputParams(owlDataProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLDataProperty> getChildren(@Nonnull OWLDataProperty owlDataProperty,
                                                   @Nonnull ProjectId projectId,
                                                   @Nonnull BranchId branchId,
                                                   @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(DATA_PROPERTY_CHILDREN_QUERY, createInputParams(owlDataProperty, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLDataProperty> getTopChildren(@Nonnull ProjectId projectId,
                                                      @Nonnull BranchId branchId,
                                                      @Nonnull OntologyDocumentId ontoDocId) {
    return getProperties(DATA_PROPERTY_CHILDREN_OF_OWL_TOP_DATA_PROPERTY_QUERY, createInputParams(projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public Collection<List<OWLDataProperty>> getPathsToRoot(@Nonnull OWLDataProperty owlDataProperty,
                                                          @Nonnull ProjectId projectId,
                                                          @Nonnull BranchId branchId,
                                                          @Nonnull OntologyDocumentId ontoDocId) {
    return getPathsToAncestor(owlDataProperty, projectId, branchId, ontoDocId)
        .stream()
        .map(DataPropertyAncestorPath::asOrderedList)
        .map(ImmutableList::reverse)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(@Nonnull OWLDataProperty parent,
                            @Nonnull OWLDataProperty child,
                            @Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId ontoDocId) {
    return getAncestors(child, projectId, branchId, ontoDocId).contains(parent);
  }

  @Override
  public boolean isLeaf(@Nonnull OWLDataProperty owlDataProperty,
                        @Nonnull ProjectId projectId,
                        @Nonnull BranchId branchId,
                        @Nonnull OntologyDocumentId ontoDocId) {
    return getChildren(owlDataProperty, projectId, branchId, ontoDocId).size() == 0;
  }

  @Nonnull
  private ImmutableSet<OWLDataProperty> getProperties(String queryString, Value inputParams) {
    return graphReader.getNodes(queryString, inputParams)
        .stream()
        .map(node -> node.get(PropertyFields.IRI).asString())
        .map(IRI::create)
        .map(dataFactory::getOWLDataProperty)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private ImmutableList<DataPropertyAncestorPath> getPathsToAncestor(OWLDataProperty ancestor,
                                                                     ProjectId projectId,
                                                                     BranchId branchId,
                                                                     OntologyDocumentId ontoDocId) {
    var ancestorPaths = ImmutableList.<DataPropertyAncestorPath>builder();
    graphReader.getPaths(PATHS_TO_ANCESTOR_QUERY, createInputParams(ancestor, projectId, branchId, ontoDocId))
        .stream()
        .map(path -> Streams.stream(path.nodes())
            .map(node -> node.get(PropertyFields.IRI).asString())
            .map(IRI::create)
            .map(dataFactory::getOWLDataProperty)
            .collect(ImmutableList.toImmutableList()))
        .map(DataPropertyAncestorPath::get)
        .forEach(ancestorPaths::add);
    return ancestorPaths.build();
  }

  @Nonnull
  private static Value createInputParams(OWLDataProperty owlDataProperty,
                                         ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(owlDataProperty.getIRI(), projectId, branchId, ontoDocId);
  }

  @Nonnull
  private static Value createInputParams(ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forContext(projectId, branchId, ontoDocId);
  }
}
