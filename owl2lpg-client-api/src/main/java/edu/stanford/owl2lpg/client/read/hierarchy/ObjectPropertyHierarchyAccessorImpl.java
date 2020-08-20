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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ObjectPropertyHierarchyAccessorImpl implements ObjectPropertyHierarchyAccessor {

  private static final String OBJECT_PROPERTY_ANCESTOR_QUERY_FILE = "hierarchy/object-property-ancestor.cpy";
  private static final String OBJECT_PROPERTY_DESCENDANT_QUERY_FILE = "hierarchy/object-property-descendant.cpy";

  private static final String OBJECT_PROPERTY_ANCESTOR_QUERY = read(OBJECT_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String OBJECT_PROPERTY_DESCENDANT_QUERY = read(OBJECT_PROPERTY_DESCENDANT_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public ObjectPropertyHierarchyAccessorImpl(@Nonnull Driver driver,
                                             @Nonnull OWLDataFactory dataFactory) {
    this.driver = checkNotNull(driver);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public OWLObjectProperty getOwlTopObjectProperty() {
    return dataFactory.getOWLTopObjectProperty();
  }

  @Override
  public ImmutableSet<OWLObjectProperty> getAncestors(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getAncestorPaths(owlObjectProperty, context)
        .stream()
        .flatMap(ObjectPropertyAncestorPath::getAncestors)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public ImmutableSet<OWLObjectProperty> getDescendants(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getDescendantPaths(owlObjectProperty, context)
        .stream()
        .flatMap(ObjectPropertyDescendantPath::getDescendants)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public ImmutableSet<OWLObjectProperty> getParents(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getAncestorPaths(owlObjectProperty, context)
        .stream()
        .map(path -> path.getAncestorAt(1))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public ImmutableSet<OWLObjectProperty> getChildren(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getDescendantPaths(owlObjectProperty, context)
        .stream()
        .map(path -> path.getDescendantAt(1))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public ImmutableSet<List<OWLObjectProperty>> getPathsToRoot(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getAncestorPaths(owlObjectProperty, context)
        .stream()
        .map(ObjectPropertyAncestorPath::asOrderedList)
        .map(ImmutableList::reverse)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(OWLObjectProperty parent, OWLObjectProperty child, AxiomContext context) {
    return getAncestors(child, context).contains(parent);
  }

  @Override
  public boolean isLeaf(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getDescendantPaths(owlObjectProperty, context).size() == 0;
  }

  @Nonnull
  private ImmutableList<ObjectPropertyAncestorPath> getAncestorPaths(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var args = Parameters.forEntity(context, owlObjectProperty);
        var result = tx.run(OBJECT_PROPERTY_ANCESTOR_QUERY, args);
        var ancestorPaths = Lists.<ObjectPropertyAncestorPath>newArrayList();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                var orderedAncestorList = Streams.stream(path.nodes())
                    .map(node -> node.get(PropertyFields.IRI).asString())
                    .map(IRI::create)
                    .map(dataFactory::getOWLObjectProperty)
                    .collect(ImmutableList.toImmutableList());
                var ancestorPath = ObjectPropertyAncestorPath.get(orderedAncestorList);
                ancestorPaths.add(ancestorPath);
              }
            }
          }
        }
        return ImmutableList.copyOf(ancestorPaths);
      });
    }
  }

  private ImmutableList<ObjectPropertyDescendantPath> getDescendantPaths(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var args = Parameters.forEntity(context, owlObjectProperty);
        var result = tx.run(OBJECT_PROPERTY_DESCENDANT_QUERY, args);
        var descendantPaths = Lists.<ObjectPropertyDescendantPath>newArrayList();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                var orderedDescendantList = Streams.stream(path.nodes())
                    .map(node -> node.get(PropertyFields.IRI).asString())
                    .map(IRI::create)
                    .map(dataFactory::getOWLObjectProperty)
                    .collect(ImmutableList.toImmutableList());
                var descendantPath = ObjectPropertyDescendantPath.get(orderedDescendantList);
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
