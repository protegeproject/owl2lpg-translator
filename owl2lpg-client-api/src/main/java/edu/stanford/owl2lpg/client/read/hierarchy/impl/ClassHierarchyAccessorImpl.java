package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.hierarchy.ClassHierarchyRoot;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.hierarchy.ClassHierarchyAccessor;
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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.DataFactory.getOWLThing;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassHierarchyAccessorImpl implements ClassHierarchyAccessor {

  private static final String CLASS_CHILDREN_OF_OWL_THING_QUERY_FILE = "hierarchy/class-children-of-owl-thing.cpy";
  private static final String CLASS_ANCESTOR_QUERY_FILE = "hierarchy/class-ancestor.cpy";
  private static final String CLASS_PARENTS_QUERY_FILE = "hierarchy/class-parents.cpy";
  private static final String CLASS_DESCENDANT_QUERY_FILE = "hierarchy/class-descendant.cpy";
  private static final String CLASS_CHILDREN_QUERY_FILE = "hierarchy/class-children.cpy";
  private static final String CLASS_PATHS_TO_ANCESTOR_QUERY_FILE = "hierarchy/class-paths-to-ancestor.cpy";

  private static final String CLASS_CHILDREN_OF_OWL_THING_QUERY = read(CLASS_CHILDREN_OF_OWL_THING_QUERY_FILE);
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
  private final EntityAccessor entityAccessor;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public ClassHierarchyAccessorImpl(@Nonnull @ClassHierarchyRoot OWLClass root,
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
  public ImmutableSet<OWLClass> getRoots(@Nonnull ProjectId projectId,
                                         @Nonnull BranchId branchId,
                                         @Nonnull OntologyDocumentId ontoDocId) {
    return ImmutableSet.of(root);
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLClass> getAncestors(@Nonnull OWLClass owlClass,
                                             @Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull OntologyDocumentId ontoDocId) {
    return getClasses(CLASS_ANCESTOR_QUERY, createInputParams(owlClass, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLClass> getDescendants(@Nonnull OWLClass owlClass,
                                               @Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull OntologyDocumentId ontoDocId) {
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return getAllClasses(projectId, branchId, ontoDocId);
    } else {
      return getClasses(CLASS_DESCENDANT_QUERY, createInputParams(owlClass, projectId, branchId, ontoDocId));
    }
  }

  @Nonnull
  private ImmutableSet<OWLClass> getAllClasses(@Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getEntitiesByType(EntityType.CLASS, projectId, branchId, ontoDocId)
        .stream()
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLClass> getParents(@Nonnull OWLClass owlClass,
                                           @Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId,
                                           @Nonnull OntologyDocumentId ontoDocId) {
    return getClasses(CLASS_PARENTS_QUERY, createInputParams(owlClass, projectId, branchId, ontoDocId));
  }

  @Override
  @Nonnull
  public ImmutableSet<OWLClass> getChildren(@Nonnull OWLClass owlClass,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId) {
    var children = ImmutableSet.<OWLClass>builder();
    children.addAll(getClasses(CLASS_CHILDREN_QUERY, createInputParams(owlClass, projectId, branchId, ontoDocId)));
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      children.addAll(getClasses(CLASS_CHILDREN_OF_OWL_THING_QUERY, createInputParams(projectId, branchId, ontoDocId)));
    }
    return children.build();
  }

  @Override
  @Nonnull
  public ImmutableSet<List<OWLClass>> getPathsToRoot(@Nonnull OWLClass owlClass,
                                                     @Nonnull ProjectId projectId,
                                                     @Nonnull BranchId branchId,
                                                     @Nonnull OntologyDocumentId ontoDocId) {
    return getPathsToAncestor(owlClass, projectId, branchId, ontoDocId)
        .stream()
        .map(ClassAncestorPath::asOrderedList)
        .map(ImmutableList::reverse)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean isAncestor(@Nonnull OWLClass parent,
                            @Nonnull OWLClass child,
                            @Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId ontoDocId) {
    return getAncestors(child, projectId, branchId, ontoDocId).contains(parent);
  }

  @Override
  public boolean isLeaf(@Nonnull OWLClass owlClass,
                        @Nonnull ProjectId projectId,
                        @Nonnull BranchId branchId,
                        @Nonnull OntologyDocumentId ontoDocId) {
    return getChildren(owlClass, projectId, branchId, ontoDocId).size() == 0;
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
  private ImmutableList<ClassAncestorPath> getPathsToAncestor(OWLClass ancestor,
                                                              ProjectId projectId,
                                                              BranchId branchId,
                                                              OntologyDocumentId ontoDocId) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var ancestorPaths = ImmutableList.<ClassAncestorPath>builder();
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
  private static Value createInputParams(OWLClass owlClass,
                                         ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(owlClass.getIRI(), projectId, branchId, ontoDocId);
  }

  @Nonnull
  private static Value createInputParams(ProjectId projectId,
                                         BranchId branchId,
                                         OntologyDocumentId ontoDocId) {
    return Parameters.forContext(projectId, branchId, ontoDocId);
  }
}
