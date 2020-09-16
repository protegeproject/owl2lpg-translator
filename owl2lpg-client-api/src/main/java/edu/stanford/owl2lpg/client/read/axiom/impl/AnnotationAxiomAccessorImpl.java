package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AnnotationAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationAxiomAccessorImpl implements AnnotationAxiomAccessor {

  private static final String ANNOTATION_AXIOM_BY_SUBJECT_QUERY_FILE = "axioms/annotation-axiom-by-subject.cpy";

  private static final String ANNOTATION_AXIOM_BY_SUBJECT_QUERY = read(ANNOTATION_AXIOM_BY_SUBJECT_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AnnotationAxiomAccessorImpl(@Nonnull GraphReader graphReader,
                                     @Nonnull NodeMapper nodeMapper) {
    this.graphReader = checkNotNull(graphReader);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationAxiom> getAnnotationAxioms(@Nonnull IRI entityIri,
                                                              @Nonnull ProjectId projectId,
                                                              @Nonnull BranchId branchId,
                                                              @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(entityIri, projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(ANNOTATION_AXIOM_BY_SUBJECT_QUERY, inputParams);
    return nodeIndex.getNodes(ANNOTATION_AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotationAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private static Value createInputParams(IRI entityIri, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(entityIri, projectId, branchId, ontoDocId);
  }
}
