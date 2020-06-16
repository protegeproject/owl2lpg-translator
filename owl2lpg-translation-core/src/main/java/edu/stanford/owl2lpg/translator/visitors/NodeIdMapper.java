package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.TranslationSessionNodeObjectMultipleEncountersChecker;
import edu.stanford.owl2lpg.translator.TranslationSessionNodeObjectSingleEncounterChecker;
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

  private Map<Object, NodeId> nodeIdMapper = Maps.newHashMapWithExpectedSize(1_000_000);

  @Nonnull
  private final NodeIdProvider idProvider;

  @Nonnull
  private final NodeIdProvider digestIdProvider;

  @Nonnull
  private final TranslationSessionNodeObjectMultipleEncountersChecker translationSessionNodeObjectMultipleEncountersChecker;

  @Nonnull
  private final TranslationSessionNodeObjectSingleEncounterChecker translationSessionNodeObjectSingleEncounterChecker;

  @Inject
  public NodeIdMapper(@Nonnull @Named("counter") NodeIdProvider idProvider,
                      @Nonnull @Named("digest") NodeIdProvider digestIdProvider,
                      @Nonnull TranslationSessionNodeObjectSingleEncounterChecker translationSessionNodeObjectSingleEncounterChecker,
                      @Nonnull TranslationSessionNodeObjectMultipleEncountersChecker translationSessionNodeObjectMultipleEncountersChecker) {
    this.idProvider = checkNotNull(idProvider);
    this.digestIdProvider = checkNotNull(digestIdProvider);
    this.translationSessionNodeObjectSingleEncounterChecker = checkNotNull(
        translationSessionNodeObjectSingleEncounterChecker);
    this.translationSessionNodeObjectMultipleEncountersChecker = checkNotNull(
        translationSessionNodeObjectMultipleEncountersChecker);
  }

  @Nonnull
  public NodeId get(@Nonnull Object o) {
    return getExistingOrCreate(o);
  }

  private NodeId getExistingOrCreate(@Nonnull Object o) {
    if (translationSessionNodeObjectSingleEncounterChecker.isSingleEncounterNodeObject(o)) {
      return idProvider.getId(o);
    } else {
      return getExistingNodeId(o);
    }
  }

  private NodeId getExistingNodeId(@Nonnull Object o) {
    var nodeId = nodeIdMapper.get(o);
    if (nodeId == null) {
      nodeId = idProvider.getId(o);
      nodeIdMapper.put(o, nodeId);
    }
    return nodeId;
  }
}
