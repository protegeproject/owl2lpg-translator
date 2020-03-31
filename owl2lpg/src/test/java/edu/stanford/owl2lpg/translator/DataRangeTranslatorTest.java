package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLFacet;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

public class DataRangeTranslatorTest {

  private DataRangeTranslator translator;

  @Before
  public void createTranslator() {
    translator = new DataRangeTranslator();
  }

  @Test
  public void shouldTranslateDataComplementOf() {
    final OWLDataComplementOf dr = DataComplementOf(Datatype(IRI("http://www.w3.org/2001/XMLSchema#positiveInteger")));
    Graph actualGraph = dr.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.DATA_COMPLEMENT_OF),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#positiveInteger").build()),
                    EdgeLabels.ENTITY_IRI
                )
            ),
            EdgeLabels.DATA_RANGE
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateDataOneOf() {
    final OWLDataOneOf dr = DataOneOf(
        Literal("a"),
        Literal("b"),
        Literal("c"));
    Graph actualGraph = dr.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.DATA_ONE_OF),
            Graph(
                Edge(
                    Node(NodeLabels.LITERAL, PropertiesBuilder.create()
                        .set(PropertyNames.LEXICAL_FORM, "a").build()),
                    Graph(
                        Edge(
                            Node(NodeLabels.DATATYPE),
                            Node(NodeLabels.IRI, PropertiesBuilder.create()
                                .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#string").build()),
                            EdgeLabels.ENTITY_IRI
                        )
                    ),
                    EdgeLabels.DATATYPE
                )
            ),
            EdgeLabels.LITERAL
        ),
        Edge(
            Node(NodeLabels.DATA_ONE_OF),
            Graph(
                Edge(
                    Node(NodeLabels.LITERAL, PropertiesBuilder.create()
                        .set(PropertyNames.LEXICAL_FORM, "b").build()),
                    Graph(
                        Edge(
                            Node(NodeLabels.DATATYPE),
                            Node(NodeLabels.IRI, PropertiesBuilder.create()
                                .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#string").build()),
                            EdgeLabels.ENTITY_IRI
                        )
                    ),
                    EdgeLabels.DATATYPE
                )
            ),
            EdgeLabels.LITERAL
        ),
        Edge(
            Node(NodeLabels.DATA_ONE_OF),
            Graph(
                Edge(
                    Node(NodeLabels.LITERAL, PropertiesBuilder.create()
                        .set(PropertyNames.LEXICAL_FORM, "c").build()),
                    Graph(
                        Edge(
                            Node(NodeLabels.DATATYPE),
                            Node(NodeLabels.IRI, PropertiesBuilder.create()
                                .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#string").build()),
                            EdgeLabels.ENTITY_IRI
                        )
                    ),
                    EdgeLabels.DATATYPE
                )
            ),
            EdgeLabels.LITERAL
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateDataIntersectionOf() {
    final OWLDataIntersectionOf dr = DataIntersectionOf(
        Datatype(IRI("http://www.w3.org/2001/XMLSchema#negativeInteger")),
        Datatype(IRI("http://www.w3.org/2001/XMLSchema#positiveInteger")));
    Graph actualGraph = dr.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.DATA_INTERSECTION_OF),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#negativeInteger").build()),
                    EdgeLabels.ENTITY_IRI
                )
            ),
            EdgeLabels.DATA_RANGE
        ),
        Edge(
            Node(NodeLabels.DATA_INTERSECTION_OF),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#positiveInteger").build()),
                    EdgeLabels.ENTITY_IRI
                )
            ),
            EdgeLabels.DATA_RANGE
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateDataUnionOf() {
    final OWLDataUnionOf dr = DataUnionOf(
        Datatype(IRI("http://www.w3.org/2001/XMLSchema#string")),
        Datatype(IRI("http://www.w3.org/2001/XMLSchema#integer")));
    Graph actualGraph = dr.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.DATA_UNION_OF),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#integer").build()),
                    EdgeLabels.ENTITY_IRI
                )
            ),
            EdgeLabels.DATA_RANGE
        ),
        Edge(
            Node(NodeLabels.DATA_UNION_OF),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#string").build()),
                    EdgeLabels.ENTITY_IRI
                )
            ),
            EdgeLabels.DATA_RANGE
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateFacetRestriction() {
    final OWLFacetRestriction dr = FacetRestriction(OWLFacet.MIN_INCLUSIVE, Literal(5));
    Graph actualGraph = dr.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.FACET_RESTRICTION),
            Node(NodeLabels.IRI, PropertiesBuilder.create()
                .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#minInclusive").build()),
            EdgeLabels.CONSTRAINING_FACET
        ),
        Edge(
            Node(NodeLabels.FACET_RESTRICTION),
            Graph(
                Edge(
                    Node(NodeLabels.LITERAL, PropertiesBuilder.create()
                        .set(PropertyNames.LEXICAL_FORM, "5").build()),
                    Graph(
                        Edge(
                            Node(NodeLabels.DATATYPE),
                            Node(NodeLabels.IRI, PropertiesBuilder.create()
                                .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#integer").build()),
                            EdgeLabels.ENTITY_IRI
                        )
                    ),
                    EdgeLabels.DATATYPE
                )
            ),
            EdgeLabels.RESTRICTION_VALUE
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateDatatypeRestriction() {
    final OWLDatatypeRestriction dr = DatatypeRestriction(
        Datatype(IRI("http://www.w3.org/2001/XMLSchema#integer")),
        FacetRestriction(OWLFacet.MIN_INCLUSIVE, Literal(5)),
        FacetRestriction(OWLFacet.MAX_EXCLUSIVE, Literal(10)));
    Graph actualGraph = dr.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.DATATYPE_RESTRICTION),
            Graph(
                Edge(
                    Node(NodeLabels.DATATYPE),
                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#integer").build()),
                    EdgeLabels.ENTITY_IRI
                )
            ),
            EdgeLabels.DATATYPE
        ),
        Edge(
            Node(NodeLabels.DATATYPE_RESTRICTION),
            Graph(
                Edge(
                    Node(NodeLabels.FACET_RESTRICTION),
                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#minInclusive").build()),
                    EdgeLabels.CONSTRAINING_FACET
                ),
                Edge(
                    Node(NodeLabels.FACET_RESTRICTION),
                    Graph(
                        Edge(
                            Node(NodeLabels.LITERAL, PropertiesBuilder.create()
                                .set(PropertyNames.LEXICAL_FORM, "5").build()),
                            Graph(
                                Edge(
                                    Node(NodeLabels.DATATYPE),
                                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#integer").build()),
                                    EdgeLabels.ENTITY_IRI
                                )
                            ),
                            EdgeLabels.DATATYPE
                        )
                    ),
                    EdgeLabels.RESTRICTION_VALUE
                )
            ),
            EdgeLabels.RESTRICTION
        ),
        Edge(
            Node(NodeLabels.DATATYPE_RESTRICTION),
            Graph(
                Edge(
                    Node(NodeLabels.FACET_RESTRICTION),
                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#maxExclusive").build()),
                    EdgeLabels.CONSTRAINING_FACET
                ),
                Edge(
                    Node(NodeLabels.FACET_RESTRICTION),
                    Graph(
                        Edge(
                            Node(NodeLabels.LITERAL, PropertiesBuilder.create()
                                .set(PropertyNames.LEXICAL_FORM, "10").build()),
                            Graph(
                                Edge(
                                    Node(NodeLabels.DATATYPE),
                                    Node(NodeLabels.IRI, PropertiesBuilder.create()
                                        .set(PropertyNames.IRI, "http://www.w3.org/2001/XMLSchema#integer").build()),
                                    EdgeLabels.ENTITY_IRI
                                )
                            ),
                            EdgeLabels.DATATYPE
                        )
                    ),
                    EdgeLabels.RESTRICTION_VALUE
                )
            ),
            EdgeLabels.RESTRICTION
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }
}