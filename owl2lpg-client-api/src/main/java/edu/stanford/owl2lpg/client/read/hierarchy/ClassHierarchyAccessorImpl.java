package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyRoot;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
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
public class ClassHierarchyAccessorImpl implements ClassHierarchyAccessor {

  private static final String CLASS_CHILDREN_OF_ROOT_QUERY_FILE = "hierarchy/class-children-of-root.cpy";
  private static final String CLASS_ANCESTOR_QUERY_FILE = "hierarchy/class-ancestor.cpy";
  private static final String CLASS_PARENTS_QUERY_FILE = "hierarchy/class-parents.cpy";
  private static final String CLASS_DESCENDANT_QUERY_FILE = "hierarchy/class-descendant.cpy";
  private static final String CLASS_CHILDREN_QUERY_FILE = "hierarchy/class-children.cpy";
  private static final String CLASS_PATHS_TO_ANCESTOR_QUERY_FILE = "hierarchy/class-paths-to-ancestor.cpy";

  private static final String CLASS_CHILDREN_OF_ROOT_QUERY = read(CLASS_CHILDREN_OF_ROOT_QUERY_FILE);
  private static final String CLASS_ANCESTOR_QUERY = read(CLASS_ANCESTOR_QUERY_FILE);
  private static final String CLASS_PARENTS_QUERY = read(CLASS_PARENTS_QUERY_FILE);
  private static final String CLASS_DESCENDANT_QUERY = read(CLASS_DESCENDANT_QUERY_FILE);
  private static final String CLASS_CHILDREN_QUERY = read(CLASS_CHILDREN_QUERY_FILE);
  private static final String PATHS_TO_ANCESTOR_QUERY = read(CLASS_PATHS_TO_ANCESTOR_QUERY_FILE);

  @Nonnull
  private final OWLClass root;

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public ClassHierarchyAccessorImpl(@Nonnull @ClassHierarchyRoot OWLClass root,
                                    @Nonnull Driver driver,
                                    @Nonnull OWLDataFactory dataFactory) {
    this.root = checkNotNull(root);
    this.driver = checkNotNull(driver);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public ImmutableSet<OWLClass> getRoots(AxiomContext context) {
    return ImmutableSet.of(root);
  }

  @Override
  public ImmutableSet<OWLClass> getAncestors(OWLClass owlClass, AxiomContext context) {
    return getClasses(CLASS_ANCESTOR_QUERY, createInputParams(owlClass, context));
  }

  @Override
  public ImmutableSet<OWLClass> getDescendants(OWLClass owlClass, AxiomContext context) {
    return getClasses(CLASS_DESCENDANT_QUERY, createInputParams(owlClass, context));
  }

  @Override
  public ImmutableSet<OWLClass> getParents(OWLClass owlClass, AxiomContext context) {
    return getClasses(CLASS_PARENTS_QUERY, createInputParams(owlClass, context));
  }

  @Override
  public ImmutableSet<OWLClass> getChildren(OWLClass owlClass, AxiomContext context) {
    var children = ImmutableSet.<OWLClass>builder();
    children.addAll(getClasses(CLASS_CHILDREN_QUERY, createInputParams(owlClass, context)));
    if (root.equals(owlClass)) {
      children.addAll(getClasses(CLASS_CHILDREN_OF_ROOT_QUERY, Parameters.forContext(context)));
    }
    return children.build();
  }

  @Override
  public ImmutableSet<List<OWLClass>> getPathsToRoot(OWLClass owlClass, AxiomContext context) {
    return getPathsToAncestor(owlClass, context)
        .stream()
        .map(ClassAncestorPath::asOrderedList)
        .map(ImmutableList::reverse)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(OWLClass parent, OWLClass child, AxiomContext context) {
    return getAncestors(child, context).contains(parent);
  }

  @Override
  public boolean isLeaf(OWLClass owlClass, AxiomContext context) {
    return getChildren(owlClass, context).size() == 0;
  }

  @Nonnull
  private ImmutableSet<OWLClass> getClasses(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var output = ImmutableSet.<OWLClass>builder();
        var result = tx.run(queryString, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("n")) {
              var node = (Node) column.getValue();
              var iri = IRI.create(node.get(PropertyFields.IRI).asString());
              var owlClass = dataFactory.getOWLClass(iri);
              output.add(owlClass);
            }
          }
        }
        return output.build();
      });
    }
  }

  @Nonnull
  private ImmutableList<ClassAncestorPath> getPathsToAncestor(OWLClass ancestor, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var ancestorPaths = ImmutableList.<ClassAncestorPath>builder();
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
                    .map(dataFactory::getOWLClass)
                    .collect(ImmutableList.toImmutableList());
                var ancestorPath = ClassAncestorPath.get(orderedAncestorList);
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
  private static Value createInputParams(OWLClass owlClass, AxiomContext context) {
    return Parameters.forEntity(context, owlClass);
  }
}
