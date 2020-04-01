package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.Edge;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.Translation.MainNode;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.LEXICAL_FORM;

/**
 * A visitor that contains the implementation to translate the OWL 2 literals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataVisitor extends HasIriVisitor
    implements OWLDataVisitorEx<Translation> {

  @Nonnull
  private final OWLEntityVisitorEx<Translation> entityVisitor;

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
    var literalNode = Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, lt.getLiteral()));
    var datatypeTranslation = lt.getDatatype().accept(this);
    if (lt.isRDFPlainLiteral()) {
      var languageTagNode = Node(NodeLabels.LANGUAGE_TAG, Properties(PropertyNames.LANGUAGE, lt.getLang()));
      return Translation.create(literalNode,
          ImmutableList.of(
              Edge(literalNode, MainNode(datatypeTranslation), EdgeLabels.DATATYPE),
              Edge(literalNode, languageTagNode, EdgeLabels.LANGUAGE_TAG)),
          ImmutableList.of(
              datatypeTranslation));
    } else {
      return Translation.create(literalNode,
          ImmutableList.of(
              Edge(literalNode, MainNode(datatypeTranslation), EdgeLabels.DATATYPE)),
          ImmutableList.of(
              datatypeTranslation));
    }
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataComplementOf dr) {
    var complementNode = Node(NodeLabels.DATA_COMPLEMENT_OF);
    var dataRangeTranslation = dr.getDataRange().accept(this);
    return Translation.create(complementNode,
        ImmutableList.of(
            Edge(complementNode, MainNode(dataRangeTranslation), EdgeLabels.DATA_RANGE)),
        ImmutableList.of(
            dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataOneOf dr) {
    var enumerationNode = Node(NodeLabels.DATA_ONE_OF);
    var translations = dr.getValues().stream()
        .map(value -> value.accept(this))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(enumerationNode, MainNode(translation), EdgeLabels.LITERAL))
        .collect(Collectors.toList());
    return Translation.create(enumerationNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataIntersectionOf dr) {
    var intersectionNode = Node(NodeLabels.DATA_INTERSECTION_OF);
    var translations = dr.getOperands().stream()
        .map(operand -> operand.accept(this))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(intersectionNode, MainNode(translation), EdgeLabels.DATA_RANGE))
        .collect(Collectors.toList());
    return Translation.create(intersectionNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataUnionOf dr) {
    var unionNode = Node(NodeLabels.DATA_UNION_OF);
    var translations = dr.getOperands().stream()
        .map(operand -> operand.accept(this))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(unionNode, MainNode(translation), EdgeLabels.DATA_RANGE))
        .collect(Collectors.toList());
    return Translation.create(unionNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeRestriction dr) {
    var restrictionNode = Node(NodeLabels.DATATYPE_RESTRICTION);
    var translations = dr.getFacetRestrictions().stream()
        .map(facet -> facet.accept(this))
        .collect(Collectors.toList());
    var edges = translations.stream()
        .map(translation -> Edge(restrictionNode, MainNode(translation), EdgeLabels.RESTRICTION))
        .collect(Collectors.toList());
    var datatypeTranslation = dr.getDatatype().accept(this);
    translations.add(0, datatypeTranslation);
    edges.add(0, Edge(restrictionNode, MainNode(datatypeTranslation), EdgeLabels.DATATYPE));
    return Translation.create(restrictionNode,
        ImmutableList.copyOf(edges),
        ImmutableList.copyOf(translations));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFacetRestriction facet) {
    var facetRestrictionNode = Node(NodeLabels.FACET_RESTRICTION);
    var facetNode = createIriNode(facet.getFacet());
    var literalTranslation = facet.getFacetValue().accept(this);
    return Translation.create(facetRestrictionNode,
        ImmutableList.of(
            Edge(facetRestrictionNode, facetNode, EdgeLabels.CONSTRAINING_FACET),
            Edge(facetRestrictionNode, MainNode(literalTranslation), EdgeLabels.RESTRICTION_VALUE)),
        ImmutableList.of(
            literalTranslation));
  }
}
