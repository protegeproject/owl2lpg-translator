package edu.stanford.owl2lpg.client.read.axiom;

import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM_ANNOTATION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DECLARATION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityDeclarationHandler implements NodeHandler<OWLDeclarationAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public EntityDeclarationHandler(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public String getLabel() {
    return DECLARATION.getMainLabel();
  }

  @Override
  public OWLDeclarationAxiom handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var entity = toEntity(node, nodeIndex, nodeMapper);
    var annotations = toAxiomAnnotations(node, nodeIndex, nodeMapper);
    return dataFactory.getOWLDeclarationAxiom(entity, annotations);
  }

  private OWLEntity toEntity(Node startNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var endNode = nodeIndex.getEndNode(startNode, ENTITY.name());
    return nodeMapper.toObject(endNode, nodeIndex, OWLEntity.class);
  }

  private Set<OWLAnnotation> toAxiomAnnotations(Node startNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var endNodes = nodeIndex.getEndNodes(startNode, AXIOM_ANNOTATION.name());
    return nodeMapper.toObjects(endNodes, nodeIndex, OWLAnnotation.class);
  }
}
