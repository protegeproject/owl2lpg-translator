package edu.stanford.owl2lpg.datastructure;

/**
 * Represents the node types that construct the graph data model, which it
 * can be either a simple node or a sub-graph.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class AnyNode {

  public abstract boolean isNode();

  public abstract boolean isGraph();
}
