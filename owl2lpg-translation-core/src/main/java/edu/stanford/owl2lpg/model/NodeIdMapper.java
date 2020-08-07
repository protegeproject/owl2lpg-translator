package edu.stanford.owl2lpg.model;

import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.translator.IdFormatChecker;
import edu.stanford.owl2lpg.translator.SingleEncounterNodeChecker;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@TranslationSessionScope
public class NodeIdMapper {

  @Nonnull
  private final NodeIdProvider numberIdProvider;

  @Nonnull
  private final NodeIdProvider digestIdProvider;

  @Nonnull
  private final IdFormatChecker idFormatChecker;

  @Nonnull
  private final SingleEncounterNodeChecker singleEncounterNodeChecker;

  private final Map<Object, NodeId> nodeIdMapper = Maps.newHashMapWithExpectedSize(1_000_000);

  @Inject
  public NodeIdMapper(@Nonnull @Named("number") NodeIdProvider numberIdProvider,
                      @Nonnull @Named("digest") NodeIdProvider digestIdProvider,
                      @Nonnull IdFormatChecker idFormatChecker,
                      @Nonnull SingleEncounterNodeChecker singleEncounterNodeChecker) {
    this.numberIdProvider = checkNotNull(numberIdProvider);
    this.digestIdProvider = checkNotNull(digestIdProvider);
    this.idFormatChecker = checkNotNull(idFormatChecker);
    this.singleEncounterNodeChecker = checkNotNull(singleEncounterNodeChecker);
  }

  @Nonnull
  public NodeId get(@Nonnull Object o) {
    return getExistingOrCreate(o);
  }

  private NodeId getExistingOrCreate(@Nonnull Object o) {
    var idFormat = idFormatChecker.getIdFormatFor(o);
    switch (idFormat) {
      case DIGEST:
        return digestIdProvider.getId(o);
      case NUMBER:
        if (singleEncounterNodeChecker.isSingleEncounterNodeObject(o)) {
          return numberIdProvider.getId(o);
        } else {
          return getExistingNodeId(o);
        }
      default:
        throw new RuntimeException("Failed to get or create the node id for object: " + o);
    }
  }

  private NodeId getExistingNodeId(@Nonnull Object o) {
    var nodeId = nodeIdMapper.get(o);
    if (nodeId == null) {
      nodeId = numberIdProvider.getId(o);
      nodeIdMapper.put(o, nodeId);
    }
    return nodeId;
  }
}
