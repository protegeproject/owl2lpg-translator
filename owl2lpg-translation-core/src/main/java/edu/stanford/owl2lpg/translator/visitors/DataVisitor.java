package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.DataRangeTranslator;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import edu.stanford.owl2lpg.translator.LiteralTranslator;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNaryDataRange;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATATYPE_RESTRICTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_COMPLEMENT_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_INTERSECTION_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_ONE_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_UNION_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.FACET;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.FACET_RESTRICTION;

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
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final EntityTranslator entityTranslator;

  @Nonnull
  private final DataRangeTranslator dataRangeTranslator;

  @Nonnull
  private final LiteralTranslator literalTranslator;

  @Inject
  public DataVisitor(@Nonnull NodeFactory nodeFactory,
                     @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                     @Nonnull EntityTranslator entityTranslator,
                     @Nonnull DataRangeTranslator dataRangeTranslator,
                     @Nonnull LiteralTranslator literalTranslator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.dataRangeTranslator = checkNotNull(dataRangeTranslator);
    this.literalTranslator = checkNotNull(literalTranslator);
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
    var literal = OWLLiteral2.create(lt);
    var mainNode = nodeFactory.createNode(literal, NodeLabels.LITERAL,
        Properties.create(ImmutableMap.of(
            PropertyFields.LEXICAL_FORM, literal.getLiteral(),
            PropertyFields.DATATYPE, literal.getDatatype(),
            PropertyFields.LANGUAGE, literal.getLanguage()
        )));
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();
    return Translation.create(literal, mainNode,
        edges.build(),
        translations.build());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataComplementOf dr) {
    var mainNode = nodeFactory.createNode(dr, DATA_COMPLEMENT_OF);
    var dataRangeTranslation = dataRangeTranslator.translate(dr.getDataRange());
    var dataRangeEdge = structuralEdgeFactory.getDataRangeEdge(mainNode, dataRangeTranslation.getMainNode());
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
      edges.add(structuralEdgeFactory.getLiteralEdge(mainNode, translation.getMainNode()));
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
    edges.add(structuralEdgeFactory.getDataTypeEdge(mainNode, datatypeTranslation.getMainNode()));
    var facetRestrictions = dr.getFacetRestrictions();
    for (var facet : facetRestrictions) {
      var translation = visit(facet);
      translations.add(translation);
      edges.add(structuralEdgeFactory.getRestrictionEdge(mainNode, translation.getMainNode()));
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
    var facetIri = facet.getFacet().getIRI();
    var facetNode = nodeFactory.createNode(facetIri, FACET,
        Properties.of(PropertyFields.IRI, String.valueOf(facetIri)));
    var constrainingFacetTranslation = Translation.create(facetIri, facetNode);
    translations.add(constrainingFacetTranslation);
    edges.add(structuralEdgeFactory.getConstrainingFacetEdge(mainNode, constrainingFacetTranslation.getMainNode()));
    var restrictionValueTranslation = literalTranslator.translate(facet.getFacetValue());
    translations.add(restrictionValueTranslation);
    edges.add(structuralEdgeFactory.getRestrictionValueEdge(mainNode, restrictionValueTranslation.getMainNode()));
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
      edges.add(structuralEdgeFactory.getDataRangeEdge(mainNode, translation.getMainNode()));
    }
    return Translation.create(dr, mainNode,
        edges.build(),
        translations.build());
  }
}
