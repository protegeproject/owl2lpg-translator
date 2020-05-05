package edu.stanford.owl2lpg.client.read.statement;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.neo4j.driver.types.Path;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class GraphResult {

  public static GraphResult create(@Nonnull ImmutableSet<Path.Segment> segments,
                                   @Nonnull ImmutableMap<String, Set<Path.Segment>> segmentsByEdge,
                                   @Nonnull ImmutableMap<String, Set<Path.Segment>> segmentsByStartNode,
                                   @Nonnull ImmutableMap<String, Set<Path.Segment>> segmentsByEndNode) {
    return new AutoValue_GraphResult(segments, segmentsByEdge, segmentsByStartNode, segmentsByEndNode);
  }

  public static Builder builder() {
    return new Builder();
  }

  public abstract Collection<Path.Segment> getSegments();

  public Collection<Path.Segment> getSegmentByEdge(String edgeLabel) {
    return getSegmentsByEdge().getOrDefault(edgeLabel, Sets.newHashSet());
  }

  protected abstract ImmutableMap<String, Set<Path.Segment>> getSegmentsByEdge();

  public Collection<Path.Segment> getSegmentByStartNode(String nodeLabel) {
    return getSegmentsByStartNode().getOrDefault(nodeLabel, Sets.newHashSet());
  }

  protected abstract ImmutableMap<String, Set<Path.Segment>> getSegmentsByStartNode();

  public Collection<Path.Segment> getSegmentByEndNode(String nodeLabel) {
    return getSegmentsByEndNode().getOrDefault(nodeLabel, Sets.newHashSet());
  }

  protected abstract ImmutableMap<String, Set<Path.Segment>> getSegmentsByEndNode();

  static class Builder {

    private Set<Path.Segment> mutableSegments = Sets.newHashSet();
    private Map<String, Set<Path.Segment>> mutableSegmentsByEdge = Maps.newHashMap();
    private Map<String, Set<Path.Segment>> mutableSegmentsByStartNode = Maps.newHashMap();
    private Map<String, Set<Path.Segment>> mutableSegmentsByEndNode = Maps.newHashMap();

    public Builder add(Path.Segment segment) {
      mutableSegments.add(segment);
      add(segment.relationship().type(), segment, mutableSegmentsByEdge);
      segment.start().labels().forEach(label -> add(label, segment, mutableSegmentsByStartNode));
      segment.end().labels().forEach(label -> add(label, segment, mutableSegmentsByEndNode));
      return this;
    }

    private void add(String label, Path.Segment segment, Map<String, Set<Path.Segment>> map) {
      var segments = map.get(label);
      if (segments == null) {
        segments = Sets.newHashSet();
        map.put(label, segments);
      }
      segments.add(segment);
    }

    public GraphResult build() {
      return GraphResult.create(ImmutableSet.copyOf(mutableSegments),
          ImmutableMap.copyOf(mutableSegmentsByEdge),
          ImmutableMap.copyOf(mutableSegmentsByStartNode),
          ImmutableMap.copyOf(mutableSegmentsByEndNode));
    }
  }
}
