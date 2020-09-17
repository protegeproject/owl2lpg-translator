package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SUB_ANNOTATION_PROPERTY_OF;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomAccessorImpl implements AxiomAccessor {

  private static final String ALL_AXIOM_QUERY_FILE = "axioms/all-axioms.cpy";
  private static final String AXIOM_BY_TYPE_QUERY_FILE = "axioms/axiom-by-type.cpy";
  private static final String SUB_ANNOTATION_PROPERTY_OF_AXIOMS_BY_SUPER_PROPERTY_QUERY_FILE =
      "axioms/sub-annotation-property-of-axiom-by-super-property.cpy";

  private static final String ALL_AXIOM_QUERY = read(ALL_AXIOM_QUERY_FILE);
  private static final String AXIOM_BY_TYPE_QUERY = read(AXIOM_BY_TYPE_QUERY_FILE);
  private static final String SUB_ANNOTATION_PROPERTY_OF_AXIOMS_BY_SUPER_PROPERTY_QUERY =
      read(SUB_ANNOTATION_PROPERTY_OF_AXIOMS_BY_SUPER_PROPERTY_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AxiomAccessorImpl(@Nonnull GraphReader graphReader,
                           @Nonnull NodeMapper nodeMapper) {
    this.graphReader = checkNotNull(graphReader);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = Parameters.forContext(projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(ALL_AXIOM_QUERY, inputParams);
    return collectAxiomsFromNodeIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLAxiom> collectAxiomsFromNodeIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public <T extends OWLAxiom> ImmutableSet<T> getAxiomsByType(@Nonnull AxiomType<T> axiomType,
                                                              @Nonnull ProjectId projectId,
                                                              @Nonnull BranchId branchId,
                                                              @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = Parameters.forAxiomType(axiomType, projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(AXIOM_BY_TYPE_QUERY, inputParams);
    return collectAxiomsFromIndex(nodeIndex, axiomType);
  }

  @Nonnull
  private <T extends OWLAxiom> ImmutableSet<T> collectAxiomsFromIndex(NodeIndex nodeIndex, AxiomType<T> axiomType) {
    return nodeIndex.getNodes(axiomType.getName())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, axiomType.getActualClass()))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLSubAnnotationPropertyOfAxiom>
  getSubAnnotationPropertyOfAxiomsBySuperProperty(@Nonnull OWLAnnotationProperty superProperty,
                                                  @Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(superProperty, projectId, branchId, ontoDocId);
    var nodeIndex = graphReader.getNodeIndex(SUB_ANNOTATION_PROPERTY_OF_AXIOMS_BY_SUPER_PROPERTY_QUERY, inputParams);
    return collectSubAnnotationPropertyOfAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLSubAnnotationPropertyOfAxiom> collectSubAnnotationPropertyOfAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(SUB_ANNOTATION_PROPERTY_OF.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLSubAnnotationPropertyOfAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private static Value createInputParams(OWLEntity entity, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(entity.getIRI(), projectId, branchId, ontoDocId);
  }
}
