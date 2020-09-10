package edu.stanford.owl2lpg.client.read.annotation.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessor;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.impl.NodeIndexImpl;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyAnnotationsAccessorImpl implements OntologyAnnotationsAccessor {

  private static final String ONTOLOGY_ANNOTATIONS_QUERY_FILE = "annotations/ontology-annotations.cpy";

  private static final String ONTOLOGY_ANNOTATIONS_QUERY = read(ONTOLOGY_ANNOTATIONS_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public OntologyAnnotationsAccessorImpl(@Nonnull Driver driver,
                                         @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLAnnotation> getOntologyAnnotations(@Nonnull ProjectId projectId,
                                                   @Nonnull BranchId branchId,
                                                   @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = getNodeIndex(Parameters.forContext(projectId, branchId, ontoDocId));
    return nodeIndex.getNodes(ANNOTATION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotation.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public Set<OWLAnnotationProperty> getOntologyAnnotationProperties(@Nonnull ProjectId projectId,
                                                                    @Nonnull BranchId branchId,
                                                                    @Nonnull OntologyDocumentId ontoDocId) {
    return getOntologyAnnotations(projectId, branchId, ontoDocId)
        .stream()
        .map(OWLAnnotation::getProperty)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public boolean containsAnnotationInSignature(@Nonnull OWLAnnotation annotation,
                                               @Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull OntologyDocumentId ontoDocId) {
    return getOntologyAnnotations(projectId, branchId, ontoDocId)
        .stream()
        .anyMatch(annotation::equals);
  }

  @Nonnull
  private NodeIndex getNodeIndex(Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var result = tx.run(ONTOLOGY_ANNOTATIONS_QUERY, inputParams);
        var nodeIndexBuilder = new NodeIndexImpl.Builder();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                path.spliterator().forEachRemaining(nodeIndexBuilder::add);
              }
            }
          }
        }
        return nodeIndexBuilder.build();
      });
    }
  }
}
