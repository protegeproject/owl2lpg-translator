package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.visitors.NodeIdMapper;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class NodeFactory {

    @Nonnull
    private final NodeIdMapper nodeIdMapper;

    @Inject
    public NodeFactory(@Nonnull NodeIdMapper nodeIdMapper) {
        this.nodeIdMapper = checkNotNull(nodeIdMapper);
    }

    @Nonnull
    public Node createNode(@Nonnull Object anyObject,
                              @Nonnull ImmutableList<String> nodeLabels,
                              @Nonnull Properties properties) {
        checkNotNull(anyObject);
        var nodeId = nodeIdMapper.get(anyObject);
        return Node.create(nodeId, nodeLabels, properties);
    }

    @Nonnull
    public Node createNode(@Nonnull Object object,
                           @Nonnull ImmutableList<String> nodeLabels) {
        return createNode(object, nodeLabels, Properties.empty());
    }
}
