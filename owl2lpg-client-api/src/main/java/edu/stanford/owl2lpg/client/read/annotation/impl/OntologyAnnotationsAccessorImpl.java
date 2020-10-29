package edu.stanford.owl2lpg.client.read.annotation.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessor;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyAnnotationsAccessorImpl implements OntologyAnnotationsAccessor {

  private static final String ONTOLOGY_ANNOTATIONS_QUERY_FILE = "read/annotations/ontology-annotations.cpy";

  private static final String ONTOLOGY_ANNOTATIONS_QUERY = read(ONTOLOGY_ANNOTATIONS_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public OntologyAnnotationsAccessorImpl(@Nonnull GraphReader graphReader,
                                         @Nonnull NodeMapper nodeMapper) {
    this.graphReader = checkNotNull(graphReader);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotation> getOntologyAnnotations(@Nonnull ProjectId projectId,
                                                            @Nonnull BranchId branchId,
                                                            @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = Parameters.forContext(projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(ONTOLOGY_ANNOTATIONS_QUERY, inputParams);
    return nodeIndex.getNodes(ANNOTATION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotation.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationProperty> getOntologyAnnotationProperties(@Nonnull ProjectId projectId,
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
}
