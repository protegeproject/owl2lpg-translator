package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.Edge;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;

/**
 * A visitor that contains the implementation to translate the OWL 2 literals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataVisitor extends VisitorBase
    implements OWLDataVisitorEx<Translation> {

  private Node mainNode;

  private final VisitorFactory visitorFactory;

  @Inject
  public DataVisitor(@Nonnull VisitorFactory visitorFactory) {
    super(visitorFactory.getNodeIdMapper());
    this.visitorFactory = checkNotNull(visitorFactory);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatype dt) {
    return visitorFactory.createEntityVisitor().visit(dt);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLLiteral lt) {
    mainNode = createLiteralNode(lt, NodeLabels.LITERAL);
    var datatypeEdge = createEdge(lt.getDatatype(), EdgeLabel.DATATYPE);
    var datatypeTranslation = createNestedTranslation(lt.getDatatype());
    if (lt.isRDFPlainLiteral() && lt.hasLang()) {
      var languageTagNode = createLanguageTagNode(lt.getLang(), NodeLabels.LANGUAGE_TAG);
      var languageTagEdge = createLanguageTagEdge(lt.getLang(), EdgeLabel.LANGUAGE_TAG);
      var languageTagTranslation = Translation.create(languageTagNode, ImmutableList.of(), ImmutableList.of());
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
    return Node(nodeIdMapper.get(languageTag),
        nodeLabels,
        Properties(PropertyFields.LANGUAGE, languageTag));
  }

  protected Edge createLanguageTagEdge(@Nonnull String languageTag,
                                       @Nonnull EdgeLabel edgeLabel) {
    var languageTagNode = createLanguageTagNode(languageTag, NodeLabels.LANGUAGE_TAG);
    return Edge(mainNode, languageTagNode, edgeLabel);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataComplementOf dr) {
    mainNode = createNode(dr, NodeLabels.DATA_COMPLEMENT_OF);
    var dataRangeEdge = createEdge(dr.getDataRange(), EdgeLabel.DATA_RANGE);
    var dataRangeTranslation = createNestedTranslation(dr.getDataRange());
    return Translation.create(mainNode,
        ImmutableList.of(dataRangeEdge),
        ImmutableList.of(dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataOneOf dr) {
    mainNode = createNode(dr, NodeLabels.DATA_ONE_OF);
    var literalEdges = createEdges(dr.getValues(), EdgeLabel.LITERAL);
    var literalTranslations = createNestedTranslations(dr.getValues());
    return Translation.create(mainNode,
        ImmutableList.copyOf(literalEdges),
        ImmutableList.copyOf(literalTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataIntersectionOf dr) {
    mainNode = createNode(dr, NodeLabels.DATA_INTERSECTION_OF);
    var dataRangeEdges = createEdges(dr.getOperands(), EdgeLabel.DATA_RANGE);
    var dataRangeTranslations = createNestedTranslations(dr.getOperands());
    return Translation.create(mainNode,
        ImmutableList.copyOf(dataRangeEdges),
        ImmutableList.copyOf(dataRangeTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataUnionOf dr) {
    mainNode = createNode(dr, NodeLabels.DATA_UNION_OF);
    var dataRangeEdges = createEdges(dr.getOperands(), EdgeLabel.DATA_RANGE);
    var dataRangeTranslations = createNestedTranslations(dr.getOperands());
    return Translation.create(mainNode,
        ImmutableList.copyOf(dataRangeEdges),
        ImmutableList.copyOf(dataRangeTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeRestriction dr) {
    mainNode = createNode(dr, NodeLabels.DATATYPE_RESTRICTION);
    var datatypeEdge = createEdge(dr.getDatatype(), EdgeLabel.DATATYPE);
    var datatypeTranslation = createNestedTranslation(dr.getDatatype());
    var facetRestrictionEdges = createEdges(dr.getFacetRestrictions(), EdgeLabel.RESTRICTION);
    var facetRestrictionTranslations = createNestedTranslations(dr.getFacetRestrictions());
    var allEdges = Lists.newArrayList(datatypeEdge);
    allEdges.addAll(facetRestrictionEdges);
    var allTranslations = Lists.newArrayList(datatypeTranslation);
    allTranslations.addAll(facetRestrictionTranslations);
    return Translation.create(mainNode,
        ImmutableList.copyOf(allEdges),
        ImmutableList.copyOf(allTranslations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFacetRestriction facet) {
    mainNode = createNode(facet, NodeLabels.FACET_RESTRICTION);
    var constrainingFacetEdge = createEdge(facet.getFacet().getIRI(), EdgeLabel.CONSTRAINING_FACET);
    var constrainingFacetTranslation = createNestedTranslation(facet.getFacet().getIRI());
    var restrictionValueEdge = createEdge(facet.getFacetValue(), EdgeLabel.RESTRICTION_VALUE);
    var restrictionValueTranslation = createNestedTranslation(facet.getFacetValue());
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
    if (anyObject instanceof OWLLiteral) {
      return getLiteralTranslation((OWLLiteral) anyObject);
    } else if (anyObject instanceof OWLDataRange) {
      return getDataRangeTranslation((OWLDataRange) anyObject);
    } else if (anyObject instanceof IRI) {
      return getIriTranslation((IRI) anyObject);
    }
    throw new IllegalArgumentException("Implementation error");
  }

  private Translation getLiteralTranslation(OWLLiteral literal) {
    var dataVisitor = visitorFactory.createDataVisitor();
    return literal.accept(dataVisitor);
  }

  private Translation getDataRangeTranslation(OWLDataRange dataRange) {
    var dataVisitor = visitorFactory.createDataVisitor();
    return dataRange.accept(dataVisitor);
  }

  private Translation getIriTranslation(IRI iri) {
    var annotationValueVisitor = visitorFactory.createAnnotationValueVisitor();
    return iri.accept(annotationValueVisitor);
  }
}
