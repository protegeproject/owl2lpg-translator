package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.hierarchy.AnnotationPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;

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

  private static final String ANNOTATION_PROPERTY_CHILDREN_OF_ROOT_QUERY_FILE = "hierarchy/annotation-property-children-of-root.cpy";
  private static final String ANNOTATION_PROPERTY_ANCESTOR_QUERY_FILE = "hierarchy/annotation-property-ancestor.cpy";
  private static final String ANNOTATION_PROPERTY_PARENTS_QUERY_FILE = "hierarchy/annotation-property-parents.cpy";
  private static final String ANNOTATION_PROPERTY_DESCENDANT_QUERY_FILE = "hierarchy/annotation-property-descendant.cpy";
  private static final String ANNOTATION_PROPERTY_CHILDREN_QUERY_FILE = "hierarchy/annotation-property-children.cpy";
  private static final String ANNOTATION_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE = "hierarchy/annotation-property-paths-to-ancestor.cpy";

  private static final String PROPERTY_CHILDREN_OF_ROOT_QUERY = read(ANNOTATION_PROPERTY_CHILDREN_OF_ROOT_QUERY_FILE);
  private static final String PROPERTY_ANCESTOR_QUERY = read(ANNOTATION_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String PROPERTY_PARENTS_QUERY = read(ANNOTATION_PROPERTY_PARENTS_QUERY_FILE);
  private static final String PROPERTY_DESCENDANT_QUERY = read(ANNOTATION_PROPERTY_DESCENDANT_QUERY_FILE);
  private static final String PROPERTY_CHILDREN_QUERY = read(ANNOTATION_PROPERTY_CHILDREN_QUERY_FILE);
  private static final String PATHS_TO_ANCESTOR_QUERY = read(ANNOTATION_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public AnnotationPropertyHierarchyAccessorImpl(@Nonnull Driver driver,
                                                 @Nonnull OWLDataFactory dataFactory) {
    this.driver = checkNotNull(driver);
    this.dataFactory = checkNotNull(dataFactory);
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
    return getChildren(owlAnnotationProperty, projectId, branchId, ontoDocId).size() == 0;
  }

  @Nonnull
  private ImmutableSet<OWLAnnotationProperty> getAnnotationProperties(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var output = ImmutableSet.<OWLAnnotationProperty>builder();
        var result = tx.run(queryString, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("n")) {
              var node = (Node) column.getValue();
              var iri = IRI.create(node.get(PropertyFields.IRI).asString());
              var owlAnnotationProp = dataFactory.getOWLAnnotationProperty(iri);
              output.add(owlAnnotationProp);
            }
          }
        }
        return output.build();
      });
    }
  }

  @Nonnull
  private ImmutableList<AnnotationPropertyAncestorPath> getPathsToAncestor(OWLAnnotationProperty ancestor,
                                                                           ProjectId projectId,
                                                                           BranchId branchId,
                                                                           OntologyDocumentId ontoDocId) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var ancestorPaths = ImmutableList.<AnnotationPropertyAncestorPath>builder();
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
                    .map(dataFactory::getOWLAnnotationProperty)
                    .collect(ImmutableList.toImmutableList());
                var ancestorPath = AnnotationPropertyAncestorPath.get(orderedAncestorList);
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
