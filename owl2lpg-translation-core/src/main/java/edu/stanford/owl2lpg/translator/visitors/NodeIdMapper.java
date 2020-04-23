package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.model.NodeId;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NodeIdMapper {

  private Map<Object, NodeId> nodeIdMapper = Maps.newHashMap();

  @Nonnull
  private final NodeIdProvider idProvider;

  public NodeIdMapper(@Nonnull NodeIdProvider idProvider) {
    this.idProvider = checkNotNull(idProvider);
  }

  @Nonnull
  public NodeId get(@Nonnull Object o) {
    return getExistingOrCreate(o);
  }

  private NodeId getExistingOrCreate(@Nonnull Object o) {
    NodeId nodeId = nodeIdMapper.get(o);
    if (nodeId == null) {
      nodeId = createNewId();
      nodeIdMapper.put(o, nodeId);
    }
    return nodeId;
  }

  private NodeId createNewId() {
    return idProvider.getId();
  }
}
