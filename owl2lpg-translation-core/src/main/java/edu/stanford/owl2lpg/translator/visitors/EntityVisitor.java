package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A visitor that contains the implementation to translate the OWL 2 entities.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityVisitor extends VisitorBase
    implements OWLEntityVisitorEx<Translation> {

  private Node mainNode;

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClass c) {
    mainNode = createEntityNode(c, NodeLabels.CLASS);
    var entityIriEdge = createEdge(c.getIRI(), EdgeLabels.ENTITY_IRI);
    var iriTranslation = createTranslation(c.getIRI());
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatype dt) {
    mainNode = createEntityNode(dt, NodeLabels.DATATYPE);
    var entityIriEdge = createEdge(dt.getIRI(), EdgeLabels.ENTITY_IRI);
    var iriTranslation = createTranslation(dt.getIRI());
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectProperty op) {
    mainNode = createEntityNode(op, NodeLabels.OBJECT_PROPERTY);
    var entityIriEdge = createEdge(op.getIRI(), EdgeLabels.ENTITY_IRI);
    var iriTranslation = createTranslation(op.getIRI());
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataProperty dp) {
    mainNode = createEntityNode(dp, NodeLabels.DATA_PROPERTY);
    var entityIriEdge = createEdge(dp.getIRI(), EdgeLabels.ENTITY_IRI);
    var iriTranslation = createTranslation(dp.getIRI());
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty ap) {
    mainNode = createEntityNode(ap, NodeLabels.ANNOTATION_PROPERTY);
    var entityIriEdge = createEdge(ap.getIRI(), EdgeLabels.ENTITY_IRI);
    var iriTranslation = createTranslation(ap.getIRI());
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual a) {
    mainNode = createEntityNode(a, NodeLabels.NAMED_INDIVIDUAL);
    var entityIriEdge = createEdge(a.getIRI(), EdgeLabels.ENTITY_IRI);
    var iriTranslation = createTranslation(a.getIRI());
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  protected Translation createEntityTranslation(@Nonnull Node mainNode,
                                                @Nonnull ImmutableList<Edge> edges,
                                                @Nonnull ImmutableList<Translation> nestedTranslations) {
    checkNotNull(mainNode);
    checkNotNull(edges);
    checkNotNull(nestedTranslations);
    return Translation.create(mainNode, edges, nestedTranslations);
  }

  @Nonnull
  @Override
  protected Node getMainNode() {
    return mainNode;
  }

  @Nonnull
  @Override
  protected Translation getTranslation(OWLObject anyObject) {
    checkNotNull(anyObject);
    if (anyObject instanceof IRI) {
      var iriNode = createIriNode((IRI) anyObject, NodeLabels.IRI);
      return Translation.create(iriNode, ImmutableList.of(), ImmutableList.of());
    }
    throw new IllegalArgumentException("Implementation error");
  }
}
