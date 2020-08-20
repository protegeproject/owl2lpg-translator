package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.Driver;
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

  private static final String ANNOTATION_PROPERTY_ANCESTOR_QUERY_FILE = "hierarchy/annotation-property-ancestor.cpy";
  private static final String ANNOTATION_PROPERTY_DESCENDANT_QUERY_FILE = "hierarchy/annotation-property-descendant.cpy";

  private static final String ANNOTATION_PROPERTY_ANCESTOR_QUERY = read(ANNOTATION_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String ANNOTATION_PROPERTY_DESCENDANT_QUERY = read(ANNOTATION_PROPERTY_DESCENDANT_QUERY_FILE);

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
  public Collection<OWLAnnotationProperty> getAncestors(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getAncestorPaths(owlAnnotationProperty, context)
        .stream()
        .flatMap(AnnotationPropertyAncestorPath::getAncestors)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLAnnotationProperty> getDescendants(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getDescendantPaths(owlAnnotationProperty, context)
        .stream()
        .flatMap(AnnotationPropertyDescendantPath::getDescendants)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLAnnotationProperty> getParents(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getAncestorPaths(owlAnnotationProperty, context)
        .stream()
        .map(path -> path.getAncestorAt(1))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLAnnotationProperty> getChildren(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getDescendantPaths(owlAnnotationProperty, context)
        .stream()
        .map(path -> path.getDescendantAt(1))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<List<OWLAnnotationProperty>> getPathsToRoot(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    return getAncestorPaths(owlAnnotationProperty, context)
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
    return getDescendantPaths(owlAnnotationProperty, context).size() == 0;
  }

  @Nonnull
  private ImmutableList<AnnotationPropertyAncestorPath> getAncestorPaths(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var args = Parameters.forEntity(context, owlAnnotationProperty);
        var result = tx.run(ANNOTATION_PROPERTY_ANCESTOR_QUERY, args);
        var ancestorPaths = Lists.<AnnotationPropertyAncestorPath>newArrayList();
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
        return ImmutableList.copyOf(ancestorPaths);
      });
    }
  }

  @Nonnull
  private ImmutableList<AnnotationPropertyDescendantPath> getDescendantPaths(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var args = Parameters.forEntity(context, owlAnnotationProperty);
        var result = tx.run(ANNOTATION_PROPERTY_DESCENDANT_QUERY, args);
        var descendantPaths = Lists.<AnnotationPropertyDescendantPath>newArrayList();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                var orderedDescendantList = Streams.stream(path.nodes())
                    .map(node -> node.get(PropertyFields.IRI).asString())
                    .map(IRI::create)
                    .map(dataFactory::getOWLAnnotationProperty)
                    .collect(ImmutableList.toImmutableList());
                var descendantPath = AnnotationPropertyDescendantPath.get(orderedDescendantList);
                descendantPaths.add(descendantPath);
              }
            }
          }
        }
        return ImmutableList.copyOf(descendantPaths);
      });
    }
  }
}
