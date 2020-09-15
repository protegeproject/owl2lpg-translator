package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyRoot;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.hierarchy.DataPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.EntityType;
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
      "hierarchy/data-property-children-of-owl-top-data-property.cpy";
  private static final String DATA_PROPERTY_ANCESTOR_QUERY_FILE = "hierarchy/data-property-ancestor.cpy";
  private static final String DATA_PROPERTY_PARENTS_QUERY_FILE = "hierarchy/data-property-parents.cpy";
  private static final String DATA_PROPERTY_DESCENDANT_QUERY_FILE = "hierarchy/data-property-descendant.cpy";
  private static final String DATA_PROPERTY_CHILDREN_QUERY_FILE = "hierarchy/data-property-children.cpy";
  private static final String DATA_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE = "hierarchy/data-property-paths-to-ancestor.cpy";

  private static final String DATA_PROPERTY_CHILDREN_OF_OWL_TOP_DATA_PROPERTY_QUERY =
      read(DATA_PROPERTY_CHILDREN_OF_OWL_TOP_DATA_PROPERTY_QUERY_FILE);
  private static final String DATA_PROPERTY_ANCESTOR_QUERY = read(DATA_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String DATA_PROPERTY_PARENTS_QUERY = read(DATA_PROPERTY_PARENTS_QUERY_FILE);
  private static final String DATA_PROPERTY_DESCENDANT_QUERY = read(DATA_PROPERTY_DESCENDANT_QUERY_FILE);
  private static final String DATA_PROPERTY_CHILDREN_QUERY = read(DATA_PROPERTY_CHILDREN_QUERY_FILE);
  private static final String PATHS_TO_ANCESTOR_QUERY = read(DATA_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE);

  @Nonnull
  private final OWLDataProperty root;

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public DataPropertyHierarchyAccessorImpl(@Nonnull @DataPropertyHierarchyRoot OWLDataProperty root,
                                           @Nonnull Driver driver,
                                           @Nonnull EntityAccessor entityAccessor,
                                           @Nonnull OWLDataFactory dataFactory) {
    this.root = checkNotNull(root);
    this.driver = checkNotNull(driver);
    this.entityAccessor = checkNotNull(entityAccessor);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLDataProperty> getRoots(@Nonnull ProjectId projectId,
                                                @Nonnull BranchId branchId,
                                                @Nonnull OntologyDocumentId ontoDocId) {
    return ImmutableSet.of(root);
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
    if (root.equals(dataFactory.getOWLTopDataProperty()) && root.equals(owlDataProperty)) {
      return getAllDataProperties(projectId, branchId, ontoDocId);
    } else {
      return getProperties(DATA_PROPERTY_DESCENDANT_QUERY, createInputParams(owlDataProperty, projectId, branchId, ontoDocId));
    }
  }

  @Nonnull
  private ImmutableSet<OWLDataProperty> getAllDataProperties(@Nonnull ProjectId projectId,
                                                             @Nonnull BranchId branchId,
                                                             @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getEntitiesByType(EntityType.DATA_PROPERTY, projectId, branchId, ontoDocId)
        .stream()
        .collect(ImmutableSet.toImmutableSet());
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
    var children = ImmutableSet.<OWLDataProperty>builder();
    children.addAll(getProperties(DATA_PROPERTY_CHILDREN_QUERY, createInputParams(owlDataProperty, projectId, branchId, ontoDocId)));
    if (root.equals(dataFactory.getOWLTopDataProperty()) && root.equals(owlDataProperty)) {
      children.addAll(getProperties(DATA_PROPERTY_CHILDREN_OF_OWL_TOP_DATA_PROPERTY_QUERY, createInputParams(projectId, branchId, ontoDocId)));
    }
    return children.build();
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
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var output = ImmutableSet.<OWLDataProperty>builder();
        var result = tx.run(queryString, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("n")) {
              var node = (Node) column.getValue();
              var iri = IRI.create(node.get(PropertyFields.IRI).asString());
              var owlDataProp = dataFactory.getOWLDataProperty(iri);
              output.add(owlDataProp);
            }
          }
        }
        return output.build();
      });
    }
  }

  @Nonnull
  private ImmutableList<DataPropertyAncestorPath> getPathsToAncestor(OWLDataProperty ancestor,
                                                                     ProjectId projectId,
                                                                     BranchId branchId,
                                                                     OntologyDocumentId ontoDocId) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var ancestorPaths = ImmutableList.<DataPropertyAncestorPath>builder();
        var args = createInputParams(ancestor, projectId, branchId, ontoDocId);
        var result = tx.run(PATHS_TO_ANCESTOR_QUERY, args);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                var orderedAncestorList = Streams.stream(path.nodes())
                    .map(node -> node.get(PropertyFields.IRI).asString())
                    .map(IRI::create)
                    .map(dataFactory::getOWLDataProperty)
                    .collect(ImmutableList.toImmutableList());
                var ancestorPath = DataPropertyAncestorPath.get(orderedAncestorList);
                ancestorPaths.add(ancestorPath);
              }
            }
          }
        }
        return ancestorPaths.build();
      });
    }
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
