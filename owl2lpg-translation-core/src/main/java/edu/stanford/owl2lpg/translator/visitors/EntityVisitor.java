package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.GraphFactory;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.model.GraphFactory.withIdentifierFrom;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;

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
    mainNode = createMainNode(c, NodeLabels.CLASS);
    var entityIriEdge = createEntityIriEdge(c, EdgeLabels.ENTITY_IRI);
    var iriTranslation = createIriTranslation(c);
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatype dt) {
    mainNode = createMainNode(dt, NodeLabels.DATATYPE);
    var entityIriEdge = createEntityIriEdge(dt, EdgeLabels.ENTITY_IRI);
    var iriTranslation = createIriTranslation(dt);
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectProperty op) {
    mainNode = createMainNode(op, NodeLabels.OBJECT_PROPERTY);
    var entityIriEdge = createEntityIriEdge(op, EdgeLabels.ENTITY_IRI);
    var iriTranslation = createIriTranslation(op);
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataProperty dp) {
    mainNode = createMainNode(dp, NodeLabels.DATA_PROPERTY);
    var entityIriEdge = createEntityIriEdge(dp, EdgeLabels.ENTITY_IRI);
    var iriTranslation = createIriTranslation(dp);
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty ap) {
    mainNode = createMainNode(ap, NodeLabels.ANNOTATION_PROPERTY);
    var entityIriEdge = createEntityIriEdge(ap, EdgeLabels.ENTITY_IRI);
    var iriTranslation = createIriTranslation(ap);
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual a) {
    mainNode = createMainNode(a, NodeLabels.NAMED_INDIVIDUAL);
    var entityIriEdge = createEntityIriEdge(a, EdgeLabels.ENTITY_IRI);
    var iriTranslation = createIriTranslation(a);
    return createEntityTranslation(
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  @Nonnull
  protected Edge createEntityIriEdge(@Nonnull OWLEntity entity,
                                     @Nonnull String edgeLabel) {
    checkNotNull(entity);
    checkNotNull(edgeLabel);
    var iriNode = createIriNode(entity);
    return GraphFactory.Edge(mainNode, iriNode, edgeLabel);
  }

  @Nonnull
  protected Node createMainNode(@Nonnull OWLEntity entity,
                                @Nonnull ImmutableList<String> nodeLabels) {
    checkNotNull(entity);
    checkNotNull(nodeLabels);
    return Node(nodeLabels,
        Properties(PropertyNames.IRI, String.valueOf(entity.getIRI())),
        withIdentifierFrom(entity));
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
}
