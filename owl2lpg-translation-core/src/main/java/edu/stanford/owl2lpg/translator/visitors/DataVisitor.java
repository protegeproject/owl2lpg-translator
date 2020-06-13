package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.EdgeFactory;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.*;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATATYPE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATA_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.LITERAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.*;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDF_PLAIN_LITERAL;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.XSD_STRING;

/**
 * A visitor that contains the implementation to translate the OWL 2 literals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@TranslationSessionScope
public class DataVisitor implements OWLDataVisitorEx<Translation> {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Nonnull
  private final EntityTranslator entityTranslator;

  @Nonnull
  private final DataRangeTranslator dataRangeTranslator;

  @Nonnull
  private final LiteralTranslator literalTranslator;

  @Nonnull
  private final AnnotationValueTranslator annotationValueTranslator;

  @Inject
  public DataVisitor(@Nonnull NodeFactory nodeFactory,
                     @Nonnull EdgeFactory edgeFactory,
                     @Nonnull EntityTranslator entityTranslator,
                     @Nonnull DataRangeTranslator dataRangeTranslator,
                     @Nonnull LiteralTranslator literalTranslator,
                     @Nonnull AnnotationValueTranslator annotationValueTranslator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.dataRangeTranslator = checkNotNull(dataRangeTranslator);
    this.literalTranslator = checkNotNull(literalTranslator);
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatype dt) {
    return entityTranslator.translate(dt);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLLiteral lt) {
    // XXX: A temporary workaround to declare that the
    //  Literal("ABC", RDF_PLAIN_LITERAL) and
    //  Literal("ABC", XSD_STRING) are different objects.
    //  Currently, the OWLAPI asserts both are the same.
    var literal = LiteralWrapper.create(lt);
    var mainNode = nodeFactory.createNode(literal, NodeLabels.LITERAL,
        Properties.create(ImmutableMap.of(
                PropertyFields.LEXICAL_FORM, literal.getLiteral(),
                PropertyFields.DATATYPE, printEmptyForStringOrPlainLiteral(literal.getDatatype()),
                PropertyFields.LANGUAGE, literal.getLanguage()
        )));
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();
    var datatypeTranslation = entityTranslator.translate(lt.getDatatype());
    translations.add(datatypeTranslation);
    edges.add(edgeFactory.createEdge(mainNode,
        datatypeTranslation.getMainNode(),
        DATATYPE));
    return Translation.create(literal, mainNode,
        edges.build(),
        translations.build());
  }

  private static String printEmptyForStringOrPlainLiteral(String datatypeString) {
    return !(datatypeString.equals(XSD_STRING.getIRI().toString())
        || datatypeString.equals(RDF_PLAIN_LITERAL.getIRI().toString()))
        ? datatypeString
        : "";
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataComplementOf dr) {
    var mainNode = nodeFactory.createNode(dr, DATA_COMPLEMENT_OF);
    var dataRangeTranslation = dataRangeTranslator.translate(dr.getDataRange());
    var dataRangeEdge = edgeFactory.createEdge(mainNode,
        dataRangeTranslation.getMainNode(),
        DATA_RANGE);
    return Translation.create(dr, mainNode,
        ImmutableList.of(dataRangeEdge),
        ImmutableList.of(dataRangeTranslation));
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataOneOf dr) {
    var mainNode = nodeFactory.createNode(dr, DATA_ONE_OF);
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();
    var values = dr.getValues();
    for (var value : values) {
      var translation = literalTranslator.translate(value);
      translations.add(translation);
      edges.add(edgeFactory.createEdge(mainNode,
          translation.getMainNode(),
          LITERAL));
    }
    return Translation.create(dr, mainNode,
        edges.build(),
        translations.build());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataIntersectionOf dr) {
    return translateNaryDataRange(dr, DATA_INTERSECTION_OF);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataUnionOf dr) {
    return translateNaryDataRange(dr, DATA_UNION_OF);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatypeRestriction dr) {
    var mainNode = nodeFactory.createNode(dr, DATATYPE_RESTRICTION);
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();
    var datatypeTranslation = entityTranslator.translate(dr.getDatatype());
    translations.add(datatypeTranslation);
    edges.add(edgeFactory.createEdge(mainNode,
        datatypeTranslation.getMainNode(),
        DATATYPE));
    var facetRestrictions = dr.getFacetRestrictions();
    for (var facet : facetRestrictions) {
      var translation = visit(facet);
      translations.add(translation);
      edges.add(edgeFactory.createEdge(mainNode,
          translation.getMainNode(),
          RESTRICTION));
    }
    return Translation.create(dr, mainNode,
        edges.build(),
        translations.build());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLFacetRestriction facet) {
    var mainNode = nodeFactory.createNode(facet, FACET_RESTRICTION);
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();
    var constrainingFacetTranslation = annotationValueTranslator.translate(facet.getFacet().getIRI());
    translations.add(constrainingFacetTranslation);
    edges.add(edgeFactory.createEdge(mainNode,
        constrainingFacetTranslation.getMainNode(),
        CONSTRAINING_FACET));
    var restrictionValueTranslation = literalTranslator.translate(facet.getFacetValue());
    translations.add(restrictionValueTranslation);
    edges.add(edgeFactory.createEdge(mainNode,
        restrictionValueTranslation.getMainNode(),
        RESTRICTION_VALUE));
    return Translation.create(facet, mainNode,
        edges.build(),
        translations.build());
  }

  private Translation translateNaryDataRange(@Nonnull OWLNaryDataRange dr,
                                             @Nonnull NodeLabels nodeLabels) {
    var mainNode = nodeFactory.createNode(dr, nodeLabels);
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();
    var operands = dr.getOperands();
    for (var op : operands) {
      var translation = dataRangeTranslator.translate(op);
      translations.add(translation);
      edges.add(edgeFactory.createEdge(mainNode,
          translation.getMainNode(),
          DATA_RANGE));
    }
    return Translation.create(dr, mainNode,
        edges.build(),
        translations.build());
  }
}
