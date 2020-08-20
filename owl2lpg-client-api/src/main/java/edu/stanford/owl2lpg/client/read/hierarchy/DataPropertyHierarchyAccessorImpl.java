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

  private static final String DATA_PROPERTY_ANCESTOR_QUERY_FILE = "hierarchy/data-property-ancestor.cpy";
  private static final String DATA_PROPERTY_DESCENDANT_QUERY_FILE = "hierarchy/data-property-descendant.cpy";

  private static final String DATA_PROPERTY_ANCESTOR_QUERY = read(DATA_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String DATA_PROPERTY_DESCENDANT_QUERY = read(DATA_PROPERTY_DESCENDANT_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public DataPropertyHierarchyAccessorImpl(@Nonnull Driver driver,
                                           @Nonnull OWLDataFactory dataFactory) {
    this.driver = checkNotNull(driver);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public OWLDataProperty getOwlTopDataProperty() {
    return dataFactory.getOWLTopDataProperty();
  }

  @Override
  public Collection<OWLDataProperty> getAncestors(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getAncestorPaths(owlDataProperty, context)
        .stream()
        .flatMap(DataPropertyAncestorPath::getAncestors)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLDataProperty> getDescendants(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getDescendantPaths(owlDataProperty, context)
        .stream()
        .flatMap(DataPropertyDescendantPath::getDescendants)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLDataProperty> getParents(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getAncestorPaths(owlDataProperty, context)
        .stream()
        .map(path -> path.getAncestorAt(1))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<OWLDataProperty> getChildren(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getDescendantPaths(owlDataProperty, context)
        .stream()
        .map(path -> path.getDescendantAt(1))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public Collection<List<OWLDataProperty>> getPathsToRoot(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getAncestorPaths(owlDataProperty, context)
        .stream()
        .map(DataPropertyAncestorPath::asOrderedList)
        .map(ImmutableList::reverse)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(OWLDataProperty parent, OWLDataProperty child, AxiomContext context) {
    return getAncestors(child, context).contains(parent);
  }

  @Override
  public boolean isLeaf(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getDescendantPaths(owlDataProperty, context).size() == 0;
  }

  @Nonnull
  private ImmutableList<DataPropertyAncestorPath> getAncestorPaths(OWLDataProperty owlDataProperty, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var args = Parameters.forEntity(context, owlDataProperty);
        var result = tx.run(DATA_PROPERTY_ANCESTOR_QUERY, args);
        var ancestorPaths = Lists.<DataPropertyAncestorPath>newArrayList();
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
        return ImmutableList.copyOf(ancestorPaths);
      });
    }
  }

  private ImmutableList<DataPropertyDescendantPath> getDescendantPaths(OWLDataProperty owlDataProperty, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var args = Parameters.forEntity(context, owlDataProperty);
        var result = tx.run(DATA_PROPERTY_DESCENDANT_QUERY, args);
        var descendantPaths = Lists.<DataPropertyDescendantPath>newArrayList();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                var orderedDescendantList = Streams.stream(path.nodes())
                    .map(node -> node.get(PropertyFields.IRI).asString())
                    .map(IRI::create)
                    .map(dataFactory::getOWLDataProperty)
                    .collect(ImmutableList.toImmutableList());
                var descendantPath = DataPropertyDescendantPath.get(orderedDescendantList);
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
