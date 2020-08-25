package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.hierarchy.DataPropertyHierarchyRoot;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.hierarchy.DataPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataPropertyHierarchyAccessorImpl implements DataPropertyHierarchyAccessor {

  private static final String DATA_PROPERTY_CHILDREN_OF_ROOT_QUERY_FILE = "hierarchy/data-property-children-of-root.cpy";
  private static final String DATA_PROPERTY_ANCESTOR_QUERY_FILE = "hierarchy/data-property-ancestor.cpy";
  private static final String DATA_PROPERTY_PARENTS_QUERY_FILE = "hierarchy/data-property-parents.cpy";
  private static final String DATA_PROPERTY_DESCENDANT_QUERY_FILE = "hierarchy/data-property-descendant.cpy";
  private static final String DATA_PROPERTY_CHILDREN_QUERY_FILE = "hierarchy/data-property-children.cpy";
  private static final String DATA_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE = "hierarchy/data-property-paths-to-ancestor.cpy";

  private static final String PROPERTY_CHILDREN_OF_ROOT_QUERY = read(DATA_PROPERTY_CHILDREN_OF_ROOT_QUERY_FILE);
  private static final String PROPERTY_ANCESTOR_QUERY = read(DATA_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String PROPERTY_PARENTS_QUERY = read(DATA_PROPERTY_PARENTS_QUERY_FILE);
  private static final String PROPERTY_DESCENDANT_QUERY = read(DATA_PROPERTY_DESCENDANT_QUERY_FILE);
  private static final String PROPERTY_CHILDREN_QUERY = read(DATA_PROPERTY_CHILDREN_QUERY_FILE);
  private static final String PATHS_TO_ANCESTOR_QUERY = read(DATA_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE);

  @Nonnull
  private final OWLDataProperty root;

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public DataPropertyHierarchyAccessorImpl(@Nonnull @DataPropertyHierarchyRoot OWLDataProperty root,
                                           @Nonnull Driver driver,
                                           @Nonnull OWLDataFactory dataFactory) {
    this.root = checkNotNull(root);
    this.driver = checkNotNull(driver);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public ImmutableSet<OWLDataProperty> getRoots(AxiomContext context) {
    return ImmutableSet.of(root);
  }

  @Override
  public ImmutableSet<OWLDataProperty> getAncestors(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getProperties(PROPERTY_ANCESTOR_QUERY, createInputParams(owlDataProperty, context));
  }

  @Override
  public ImmutableSet<OWLDataProperty> getDescendants(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getProperties(PROPERTY_DESCENDANT_QUERY, createInputParams(owlDataProperty, context));
  }

  @Override
  public ImmutableSet<OWLDataProperty> getParents(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getProperties(PROPERTY_PARENTS_QUERY, createInputParams(owlDataProperty, context));
  }

  @Override
  public ImmutableSet<OWLDataProperty> getChildren(OWLDataProperty owlDataProperty, AxiomContext context) {
    var children = ImmutableSet.<OWLDataProperty>builder();
    children.addAll(getProperties(PROPERTY_CHILDREN_QUERY, createInputParams(owlDataProperty, context)));
    if (root.equals(owlDataProperty)) {
      children.addAll(getProperties(PROPERTY_CHILDREN_OF_ROOT_QUERY, Parameters.forContext(context)));
    }
    return children.build();
  }

  @Override
  public ImmutableSet<List<OWLDataProperty>> getPathsToRoot(OWLDataProperty owlDataProperty, AxiomContext context) {
    return getPathsToAncestor(owlDataProperty, context)
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
    return getChildren(owlDataProperty, context).size() == 0;
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
  private ImmutableList<DataPropertyAncestorPath> getPathsToAncestor(OWLDataProperty ancestor, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var ancestorPaths = ImmutableList.<DataPropertyAncestorPath>builder();
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
  private static Value createInputParams(OWLDataProperty owlDataProperty, AxiomContext context) {
    return Parameters.forEntity(context, owlDataProperty);
  }
}
