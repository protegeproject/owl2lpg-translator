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

  private static final String CLASS_ANCESTOR_QUERY_FILE = "hierarchy/class-ancestor.cpy";
  private static final String CLASS_DESCENDANT_QUERY_FILE = "hierarchy/class-descendant.cpy";

  private static final String CLASS_ANCESTOR_QUERY = read(CLASS_ANCESTOR_QUERY_FILE);
  private static final String CLASS_DESCENDANT_QUERY = read(CLASS_DESCENDANT_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public ClassHierarchyAccessorImpl(@Nonnull Driver driver,
                                    @Nonnull OWLDataFactory dataFactory) {
    this.driver = checkNotNull(driver);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public OWLClass getOwlThing() {
    return dataFactory.getOWLThing();
  }

  @Override
  public ImmutableSet<OWLClass> getAncestors(OWLClass owlClass, AxiomContext context) {
    return getAncestorPaths(owlClass, context)
        .stream()
        .flatMap(ClassAncestorPath::getAncestors)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public ImmutableSet<OWLClass> getDescendants(OWLClass owlClass, AxiomContext context) {
    return getDescendantPaths(owlClass, context)
        .stream()
        .flatMap(ClassDescendantPath::getDescendants)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public ImmutableSet<OWLClass> getParents(OWLClass owlClass, AxiomContext context) {
    return getAncestorPaths(owlClass, context)
        .stream()
        .map(path -> path.getAncestorAt(1))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public ImmutableSet<OWLClass> getChildren(OWLClass owlClass, AxiomContext context) {
    return getDescendantPaths(owlClass, context)
        .stream()
        .map(path -> path.getDescendantAt(1))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public ImmutableSet<List<OWLClass>> getPathsToRoot(OWLClass owlClass, AxiomContext context) {
    return getAncestorPaths(owlClass, context)
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
    return getDescendantPaths(owlClass, context).size() == 0;
  }

  private ImmutableList<ClassAncestorPath> getAncestorPaths(OWLClass owlClass, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var args = Parameters.forEntity(context, owlClass);
        var result = tx.run(CLASS_ANCESTOR_QUERY, args);
        var ancestorPaths = Lists.<ClassAncestorPath>newArrayList();
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
        return ImmutableList.copyOf(ancestorPaths);
      });
    }
  }

  private ImmutableList<ClassDescendantPath> getDescendantPaths(OWLClass owlClass, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var args = Parameters.forEntity(context, owlClass);
        var result = tx.run(CLASS_DESCENDANT_QUERY, args);
        var descendantPaths = Lists.<ClassDescendantPath>newArrayList();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                var orderedDescendantList = Streams.stream(path.nodes())
                    .map(node -> node.get(PropertyFields.IRI).asString())
                    .map(IRI::create)
                    .map(dataFactory::getOWLClass)
                    .collect(ImmutableList.toImmutableList());
                var descendantPath = ClassDescendantPath.get(orderedDescendantList);
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
