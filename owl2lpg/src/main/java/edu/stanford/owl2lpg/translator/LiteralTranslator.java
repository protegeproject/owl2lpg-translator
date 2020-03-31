package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;

/**
 * The translator sub-module for translating the OWL 2 literal to labelled
 * property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class LiteralTranslator extends HasIriTranslator
    implements OWLDataVisitorEx<Graph> {

  @Override
  public Graph visit(@Nonnull OWLDatatype dt) {
    Node entityNode = Node(NodeLabels.DATATYPE);
    Node iriNode = createIriNode(dt);
    return Graph(
        Edge(entityNode, iriNode, EdgeLabels.ENTITY_IRI)
    );
  }

  @Override
  public Graph visit(@Nonnull OWLLiteral lt) {
    Node literalNode = Node(NodeLabels.LITERAL, PropertiesBuilder.create()
        .set(PropertyNames.LEXICAL_FORM, lt.getLiteral()).build());
    Graph datatypeGraph = lt.getDatatype().accept(this);
    if (lt.isRDFPlainLiteral()) {
      Node languageTagNode = Node(NodeLabels.LANGUAGE_TAG, PropertiesBuilder.create()
          .set(PropertyNames.LANGUAGE, lt.getLang()).build());
      return Graph(
          Edge(literalNode, datatypeGraph, EdgeLabels.DATATYPE),
          Edge(literalNode, languageTagNode, EdgeLabels.LANGUAGE_TAG)
      );
    } else {
      return Graph(
          Edge(literalNode, datatypeGraph, EdgeLabels.DATATYPE)
      );
    }
  }
}
