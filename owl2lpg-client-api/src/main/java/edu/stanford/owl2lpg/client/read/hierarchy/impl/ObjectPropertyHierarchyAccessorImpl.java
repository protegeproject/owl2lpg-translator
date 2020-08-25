package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.hierarchy.ObjectPropertyHierarchyRoot;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.hierarchy.ObjectPropertyHierarchyAccessor;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
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

  private static final String OBJECT_PROPERTY_CHILDREN_OF_ROOT_QUERY_FILE = "hierarchy/object-property-children-of-root.cpy";
  private static final String OBJECT_PROPERTY_ANCESTOR_QUERY_FILE = "hierarchy/object-property-ancestor.cpy";
  private static final String OBJECT_PROPERTY_PARENTS_QUERY_FILE = "hierarchy/object-property-parents.cpy";
  private static final String OBJECT_PROPERTY_DESCENDANT_QUERY_FILE = "hierarchy/object-property-descendant.cpy";
  private static final String OBJECT_PROPERTY_CHILDREN_QUERY_FILE = "hierarchy/object-property-children.cpy";
  private static final String OBJECT_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE = "hierarchy/object-property-paths-to-ancestor.cpy";

  private static final String PROPERTY_CHILDREN_OF_ROOT_QUERY = read(OBJECT_PROPERTY_CHILDREN_OF_ROOT_QUERY_FILE);
  private static final String PROPERTY_ANCESTOR_QUERY = read(OBJECT_PROPERTY_ANCESTOR_QUERY_FILE);
  private static final String PROPERTY_PARENTS_QUERY = read(OBJECT_PROPERTY_PARENTS_QUERY_FILE);
  private static final String PROPERTY_DESCENDANT_QUERY = read(OBJECT_PROPERTY_DESCENDANT_QUERY_FILE);
  private static final String PROPERTY_CHILDREN_QUERY = read(OBJECT_PROPERTY_CHILDREN_QUERY_FILE);
  private static final String PATHS_TO_ANCESTOR_QUERY = read(OBJECT_PROPERTY_PATHS_TO_ANCESTOR_QUERY_FILE);

  @Nonnull
  private final OWLObjectProperty root;

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public ObjectPropertyHierarchyAccessorImpl(@Nonnull @ObjectPropertyHierarchyRoot OWLObjectProperty root,
                                             @Nonnull Driver driver,
                                             @Nonnull OWLDataFactory dataFactory) {
    this.root = checkNotNull(root);
    this.driver = checkNotNull(driver);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public ImmutableSet<OWLObjectProperty> getRoots(AxiomContext context) {
    return ImmutableSet.of(root);
  }

  @Override
  public ImmutableSet<OWLObjectProperty> getAncestors(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getProperties(PROPERTY_ANCESTOR_QUERY, createInputParams(owlObjectProperty, context));
  }

  @Override
  public ImmutableSet<OWLObjectProperty> getDescendants(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getProperties(PROPERTY_DESCENDANT_QUERY, createInputParams(owlObjectProperty, context));
  }

  @Override
  public ImmutableSet<OWLObjectProperty> getParents(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getProperties(PROPERTY_PARENTS_QUERY, createInputParams(owlObjectProperty, context));
  }

  @Override
  public ImmutableSet<OWLObjectProperty> getChildren(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    var children = ImmutableSet.<OWLObjectProperty>builder();
    children.addAll(getProperties(PROPERTY_CHILDREN_QUERY, createInputParams(owlObjectProperty, context)));
    if (root.equals(owlObjectProperty)) {
      children.addAll(getProperties(PROPERTY_CHILDREN_OF_ROOT_QUERY, createInputParams(context)));
    }
    return children.build();
  }

  public ImmutableSet<List<OWLObjectProperty>> getPathsToRoot(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return getPathsToAncestor(owlObjectProperty, context)
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
    return getChildren(owlObjectProperty, context).size() == 0;
  }

  @Nonnull
  private ImmutableSet<OWLObjectProperty> getProperties(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var output = ImmutableSet.<OWLObjectProperty>builder();
        var result = tx.run(queryString, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("n")) {
              var node = (Node) column.getValue();
              var iri = IRI.create(node.get(PropertyFields.IRI).asString());
              var owlObjectProp = dataFactory.getOWLObjectProperty(iri);
              output.add(owlObjectProp);
            }
          }
        }
        return output.build();
      });
    }
  }

  @Nonnull
  private ImmutableList<ObjectPropertyAncestorPath> getPathsToAncestor(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var ancestorPaths = ImmutableList.<ObjectPropertyAncestorPath>builder();
        var inputParams = createInputParams(owlObjectProperty, context);
        var result = tx.run(PATHS_TO_ANCESTOR_QUERY, inputParams);
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
        return ancestorPaths.build();
      });
    }
  }

  @Nonnull
  private static Value createInputParams(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    return Parameters.forEntityIri(owlObjectProperty.getIRI(), context.getProjectId(), context.getBranchId(), context.getOntologyDocumentId());
  }

  @Nonnull
  private static Value createInputParams(AxiomContext context) {
    return Parameters.forContext(context.getProjectId(), context.getBranchId(), context.getOntologyDocumentId());
  }
}