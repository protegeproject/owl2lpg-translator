package edu.stanford.owl2lpg.model;

import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import org.semanticweb.owlapi.model.OWLClassExpression;

import javax.annotation.Nonnull;
import java.util.Set;

public class EdgeFactory {

    public Edge createEdge(@Nonnull Node fromNode,
                           @Nonnull Node toNode,
                           @Nonnull EdgeLabel edgeLabel,
                           @Nonnull Properties properties) {
        return Edge.create(fromNode, toNode, edgeLabel, properties);
    }


    public void createEdges(@Nonnull Node mainNode, Set<Node> operands, EdgeLabel classExpression) {

    }
}
