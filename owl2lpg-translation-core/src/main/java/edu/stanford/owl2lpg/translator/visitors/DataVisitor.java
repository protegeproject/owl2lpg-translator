package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.*;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;

/**
 * A visitor that contains the implementation to translate the OWL 2 literals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataVisitor extends VisitorBase
    implements OWLDataVisitorEx<Translation> {

  @Nonnull
  private final OWLEntityVisitorEx<Translation> entityVisitor;

  private Node mainNode;

  @Inject
  public DataVisitor(@Nonnull OWLEntityVisitorEx<Translation> entityVisitor) {
    this.entityVisitor = checkNotNull(entityVisitor);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatype dt) {
    return entityVisitor.visit(dt);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLLiteral lt) {
    mainNode = createLiteralNode(lt, NodeLabels.LITERAL);
    var datatypeEdge = createEdge(lt.getDatatype(), EdgeLabels.DATATYPE);
    var datatypeTranslation = createTranslation(lt.getDatatype());
    if (lt.isRDFPlainLiteral() && lt.hasLang()) {
      var languageTagEdge = createLanguageTagEdge(lt.getLang(), EdgeLabels.LANGUAGE_TAG);
      var languageTagTranslation = createLanguageTagTranslation(lt.getLang());
      return Translation.create(mainNode,
          ImmutableList.of(datatypeEdge, languageTagEdge),
          ImmutableList.of(datatypeTranslation, languageTagTranslation));
    } else {
      return Translation.create(mainNode,
          ImmutableList.of(datatypeEdge),
          ImmutableList.of(datatypeTranslation));
    }
  }

  protected Node createLanguageTagNode(@Nonnull String languageTag,
                                       @Nonnull ImmutableList<String> nodeLabels) {
    return Node(nodeLabels,
        Properties(PropertyNames.LANGUAGE, languageTag),
        withIdentifierFrom(languageTag));
  }

  private Edge createLanguageTagEdge(@Nonnull String languageTag, @Nonnull String edgeLabel) {
    var languageTagNode = createLanguageTagNode(languageTag, NodeLabels.LANGUAGE_TAG);
    return Edge(mainNode, languageTagNode, edgeLabel);
  }

  private Translation createLanguageTagTranslation(@Nonnull String languageTag) {
    var languageTagNode = createLanguageTagNode(languageTag, NodeLabels.LANGUAGE_TAG);
    return Translation.create(languageTagNode, ImmutableList.of(), ImmutableList.of());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataComplementOf dr) {
    mainNode = createNode(dr, NodeLabels.DATA_COMPLEMENT_OF);
    var dataRangeEdge = createEdge(dr.getDataRange(), EdgeLabels.DATA_RANGE);
    var dataRangeTranslation = createTranslation(dr.getDataRange());
    return Translation.create(mainNode,
        ImmutableList.of(dataRangeEdge),
        ImmutableList.of(dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataOneOf dr) {
    mainNode = createNode(dr, NodeLabels.DATA_ONE_OF);
    var literalEdges = createEdges(dr.getValues(), EdgeLabels.LITERAL);
    var literalTranslations = createTranslations(dr.getValues());
    return Translation.create(mainNode,
        ImmutableList.copyOf(literalEdges),
        ImmutableList.copyOf(literalTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataIntersectionOf dr) {
    mainNode = createNode(dr, NodeLabels.DATA_INTERSECTION_OF);
    var dataRangeEdges = createEdges(dr.getOperands(), EdgeLabels.DATA_RANGE);
    var dataRangeTranslations = createTranslations(dr.getOperands());
    return Translation.create(mainNode,
        ImmutableList.copyOf(dataRangeEdges),
        ImmutableList.copyOf(dataRangeTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataUnionOf dr) {
    mainNode = createNode(dr, NodeLabels.DATA_UNION_OF);
    var dataRangeEdges = createEdges(dr.getOperands(), EdgeLabels.DATA_RANGE);
    var dataRangeTranslations = createTranslations(dr.getOperands());
    return Translation.create(mainNode,
        ImmutableList.copyOf(dataRangeEdges),
        ImmutableList.copyOf(dataRangeTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeRestriction dr) {
    mainNode = createNode(dr, NodeLabels.DATATYPE_RESTRICTION);
    var datatypeEdge = createEdge(dr.getDatatype(), EdgeLabels.DATATYPE);
    var datatypeTranslation = createTranslation(dr.getDatatype());
    var facetRestrictionEdges = createEdges(dr.getFacetRestrictions(), EdgeLabels.RESTRICTION);
    var facetRestrictionTranslations = createTranslations(dr.getFacetRestrictions());
    var allEdges = Lists.newArrayList(datatypeEdge, (Edge) facetRestrictionEdges);
    var allTranslations = Lists.newArrayList(datatypeTranslation, (Translation) facetRestrictionTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFacetRestriction facet) {
    mainNode = createNode(facet, NodeLabels.FACET_RESTRICTION);
    var constrainingFacetEdge = createEdge(facet.getFacet().getIRI(), EdgeLabels.CONSTRAINING_FACET);
    var constrainingFacetTranslation = createTranslation(facet.getFacet().getIRI());
    var restrictionValueEdge = createEdge(facet.getFacetValue(), EdgeLabels.RESTRICTION_VALUE);
    var restrictionValueTranslation = createTranslation(facet.getFacetValue());
    return Translation.create(mainNode,
        ImmutableList.of(constrainingFacetEdge, restrictionValueEdge),
        ImmutableList.of(constrainingFacetTranslation, restrictionValueTranslation));
  }

  @Override
  @Nullable
  protected Node getMainNode() {
    return mainNode;
  }

  @Nonnull
  @Override
  protected Translation getTranslation(OWLObject anyObject) {
    checkNotNull(anyObject);
    if (anyObject instanceof OWLDataRange) {
      return ((OWLDataRange) anyObject).accept(this);
    } else if (anyObject instanceof OWLLiteral) {
      return ((OWLLiteral) anyObject).accept(this);
    }
    throw new IllegalArgumentException("Implementation error");
  }
}
