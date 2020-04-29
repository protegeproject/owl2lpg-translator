package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.utils.PropertiesBuilder;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.Edge;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class VisitorBase {

  protected final NodeIdMapper nodeIdMapper;

  protected VisitorBase(@Nonnull NodeIdMapper nodeIdMapper) {
    this.nodeIdMapper = checkNotNull(nodeIdMapper);
  }

  @Nonnull
  protected Node createNode(@Nonnull Object anyObject,
                            @Nonnull ImmutableList<String> nodeLabels,
                            @Nonnull Properties properties) {
    checkNotNull(anyObject);
    checkNotNull(nodeLabels);
    checkNotNull(properties);
    return Node(nodeIdMapper.get(anyObject), nodeLabels, properties);
  }

  @Nonnull
  protected Node createNode(@Nonnull Object anyObject,
                            @Nonnull ImmutableList<String> nodeLabels) {
    checkNotNull(anyObject);
    checkNotNull(nodeLabels);
    return createNode(anyObject, nodeLabels, Properties.empty());
  }

  @Nonnull
  protected Node createEntityNode(@Nonnull OWLEntity anyEntity,
                                  @Nonnull ImmutableList<String> nodeLabels) {
    return createNode(
        anyEntity,
        nodeLabels,
        Properties(PropertyNames.IRI, String.valueOf(anyEntity.getIRI())));
  }

  @Nonnull
  protected Node createLiteralNode(@Nonnull OWLLiteral anyLiteral,
                                   @Nonnull ImmutableList<String> nodeLabels) {
    return createNode(
        anyLiteral,
        nodeLabels,
        PropertiesBuilder.create()
            .set(PropertyNames.LEXICAL_FORM, anyLiteral.getLiteral())
            .set(PropertyNames.DATATYPE, anyLiteral.getDatatype().getIRI().toString())
            .set(PropertyNames.LANGUAGE, anyLiteral.getLang())
            .build());
  }

  @Nonnull
  protected Node createIriNode(@Nonnull IRI iri,
                               @Nonnull ImmutableList<String> nodeLabels) {
    return createNode(
        iri,
        nodeLabels,
        Properties(PropertyNames.IRI, String.valueOf(iri)));
  }

  @Nonnull
  protected Node createAnonymousIndividualNode(@Nonnull OWLAnonymousIndividual individual,
                                               @Nonnull ImmutableList<String> nodeLabels) {
    return createNode(
        individual,
        nodeLabels,
        Properties(PropertyNames.NODE_ID, String.valueOf(individual.getID())));
  }

  @Nonnull
  protected Node createCardinalityNode(@Nonnull OWLObjectCardinalityRestriction restriction,
                                       @Nonnull ImmutableList<String> nodeLabels) {
    return createNode(
        restriction,
        nodeLabels,
        Properties(PropertyNames.CARDINALITY, restriction.getCardinality()));
  }

  @Nonnull
  protected Node createCardinalityNode(@Nonnull OWLDataCardinalityRestriction restriction,
                                       @Nonnull ImmutableList<String> nodeLabels) {
    return createNode(
        restriction,
        nodeLabels,
        Properties(PropertyNames.CARDINALITY, restriction.getCardinality()));
  }

  @Nonnull
  protected Edge createEdge(@Nonnull OWLObject anyObject,
                            @Nonnull String edgeLabel) {
    checkNotNull(anyObject);
    checkNotNull(edgeLabel);
    return Edge(getMainNode(), getMainNode(anyObject), edgeLabel);
  }

  @Nonnull
  protected Collection<Edge> createEdges(
      @Nonnull Set<? extends OWLObject> anyObjects,
      @Nonnull String edgeLabel) {
    checkNotNull(anyObjects);
    checkNotNull(edgeLabel);
    return anyObjects.stream()
        .map(o -> createEdge(o, edgeLabel))
        .collect(Collectors.toList());
  }

  @Nonnull
  protected Edge createAugmentedEdge(@Nonnull OWLObject fromObject,
                                     @Nonnull OWLObject toObject,
                                     @Nonnull String edgeLabel) {
    checkNotNull(fromObject);
    checkNotNull(toObject);
    checkNotNull(edgeLabel);
    return Edge(getMainNode(fromObject), getMainNode(toObject), edgeLabel);
  }

  @Nonnull
  protected Collection<Edge> createAugmentedEdges(@Nonnull OWLObject fromObject,
                                                  @Nonnull Set<? extends OWLObject> toObjects,
                                                  @Nonnull String edgeLabel) {
    checkNotNull(fromObject);
    checkNotNull(toObjects);
    checkNotNull(edgeLabel);
    return toObjects.stream()
        .map(o -> createAugmentedEdge(fromObject, o, edgeLabel))
        .collect(Collectors.toList());
  }

  @Nonnull
  protected Collection<Edge> createAugmentedEdges(@Nonnull Set<? extends OWLObject> fromObjects,
                                                  @Nonnull Set<? extends OWLObject> toObjects,
                                                  @Nonnull String edgeLabel) {
    checkNotNull(fromObjects);
    checkNotNull(toObjects);
    checkNotNull(edgeLabel);
    var augmentedEdges = Lists.<Edge>newArrayList();
    fromObjects.stream()
        .forEach(o1 -> toObjects.stream()
            .forEach(o2 -> {
              if (!o1.equals(o2)) {
                augmentedEdges.add(createAugmentedEdge(o1, o2, edgeLabel));
              }
            }));
    return augmentedEdges;
  }

  @Nonnull
  protected Translation createNestedTranslation(@Nonnull OWLObject anyObject) {
    return getTranslation(anyObject);
  }

  @Nonnull
  protected Collection<Translation> createNestedTranslations(
      @Nonnull Set<? extends OWLObject> anyObjects) {
    checkNotNull(anyObjects);
    return anyObjects.stream()
        .map(o -> createNestedTranslation(o))
        .collect(Collectors.toList());
  }

  @Nonnull
  protected Node getMainNode(OWLObject anyObject) {
    return getTranslation(anyObject).getMainNode();
  }

  protected abstract Node getMainNode();

  protected abstract Translation getTranslation(@Nonnull OWLObject anyObject);
}
