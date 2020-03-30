package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Edge;
import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;

/**
 * The translator sub-module for translating the OWL 2 data range to
 * labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataRangeTranslator extends HasIriTranslator
    implements OWLDataVisitorEx<Graph> {

  @Override
  public Graph visit(@Nonnull OWLDataComplementOf dr) {
    Node complementNode = Node(NodeLabels.DATA_COMPLEMENT_OF);
    Graph dataRangeGraph = dr.getDataRange().accept(this);
    return Graph(
        Edge(complementNode, dataRangeGraph, EdgeLabels.DATA_RANGE)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLDataOneOf dr) {
    Node enumerationNode = Node(NodeLabels.DATA_ONE_OF);
    List<Edge> listOfEdges = dr.getOperandsAsList().stream()
        .map(operand -> Edge(
            enumerationNode, operand.accept(new LiteralTranslator()), EdgeLabels.LITERAL))
        .collect(Collectors.toList());
    return Graph(listOfEdges);
  }

  @Override
  public Graph visit(@Nonnull OWLDataIntersectionOf dr) {
    Node intersectionNode = Node(NodeLabels.DATA_INTERSECTION_OF);
    List<Edge> listOfEdges = dr.getOperandsAsList().stream()
        .map(operand -> Edge(
            intersectionNode, operand.accept(this), EdgeLabels.DATA_RANGE))
        .collect(Collectors.toList());
    return Graph(listOfEdges);
  }

  @Override
  public Graph visit(@Nonnull OWLDataUnionOf dr) {
    Node intersectionNode = Node(NodeLabels.DATA_UNION_OF);
    List<Edge> listOfEdges = dr.getOperandsAsList().stream()
        .map(operand -> Edge(
            intersectionNode, operand.accept(this), EdgeLabels.DATA_RANGE))
        .collect(Collectors.toList());
    return Graph(listOfEdges);
  }

  @Override
  public Graph visit(@Nonnull OWLDatatypeRestriction dr) {
    Node restrictionNode = Node(NodeLabels.DATATYPE_RESTRICTION);
    Graph datatypeGraph = dr.getDatatype().accept(new LiteralTranslator());
    List<Edge> listOfEdges = dr.facetRestrictionsAsList().stream()
        .map(operand -> Edge(
            restrictionNode, operand.accept(this), EdgeLabels.RESTRICTION))
        .collect(Collectors.toList());
    listOfEdges.add(0, Edge(restrictionNode, datatypeGraph, EdgeLabels.DATATYPE));
    return Graph(listOfEdges);
  }

  @Override
  public Graph visit(@Nonnull OWLFacetRestriction facet) {
    Node facetRestrictionNode = Node(NodeLabels.FACET_RESTRICTION);
    Node facetNode = createIriNode(facet.getFacet());
    Graph literalGraph = facet.getFacetValue().accept(new LiteralTranslator());
    return Graph(
        Edge(facetRestrictionNode, facetNode, EdgeLabels.CONSTRAINING_FACET),
        Edge(facetRestrictionNode, literalGraph, EdgeLabels.RESTRICTION_VALUE)
    );
  }
}
