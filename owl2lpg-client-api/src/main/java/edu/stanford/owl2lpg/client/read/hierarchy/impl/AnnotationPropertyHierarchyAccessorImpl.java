package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.hierarchy.AnnotationPropertyHierarchyAccessor;
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
  public ImmutableSet<OWLAnnotationProperty> getRoots(AxiomContext context) {
    return getAnnotationProperties(PROPERTY_CHILDREN_OF_ROOT_QUERY, Parameters.forContext(context));
  }

  @Override
  public ImmutableSet<OWLAnnotationProperty> getAncestors(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getAnnotationProperties(PROPERTY_ANCESTOR_QUERY, createInputParams(owlAnnotationProperty, context));
  }

  @Override
  public ImmutableSet<OWLAnnotationProperty> getDescendants(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getAnnotationProperties(PROPERTY_DESCENDANT_QUERY, createInputParams(owlAnnotationProperty, context));
  }

  @Override
  public ImmutableSet<OWLAnnotationProperty> getParents(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getAnnotationProperties(PROPERTY_PARENTS_QUERY, createInputParams(owlAnnotationProperty, context));
  }

  @Override
  public ImmutableSet<OWLAnnotationProperty> getChildren(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getAnnotationProperties(PROPERTY_CHILDREN_QUERY, createInputParams(owlAnnotationProperty, context));
  }

  @Override
  public ImmutableSet<List<OWLAnnotationProperty>> getPathsToRoot(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getPathsToAncestor(owlAnnotationProperty, context)
        .stream()
        .map(AnnotationPropertyAncestorPath::asOrderedList)
        .map(ImmutableList::reverse)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(OWLAnnotationProperty parent, OWLAnnotationProperty child, AxiomContext context) {
    return getAncestors(child, context).contains(parent);
  }

  @Override
  public boolean isLeaf(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getChildren(owlAnnotationProperty, context).size() == 0;
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
  private ImmutableList<AnnotationPropertyAncestorPath> getPathsToAncestor(OWLAnnotationProperty ancestor, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var ancestorPaths = ImmutableList.<AnnotationPropertyAncestorPath>builder();
        var args = createInputParams(ancestor, context);
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
  private static Value createInputParams(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return Parameters.forEntity(context, owlAnnotationProperty);
  }
}
