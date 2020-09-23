package edu.stanford.owl2lpg.translator.internal;

import com.google.common.hash.HashFunction;
import edu.stanford.owl2lpg.model.EdgeId;
import edu.stanford.owl2lpg.model.EdgeIdProvider;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DigestEdgeIdProvider implements EdgeIdProvider {

  @Nonnull
  private final HashFunction hashFunction;

  public DigestEdgeIdProvider(@Nonnull HashFunction hashFunction) {
    this.hashFunction = checkNotNull(hashFunction);
  }

  @Override
  public EdgeId get(Node startId, Node endNode, EdgeLabel edgeLabel) {
    var startNodeId = startId.getNodeId().asString();
    var endNodeId = endNode.getNodeId().asString();
    var label = edgeLabel.getName();
    var edgeString = startNodeId + ":" + endNodeId + ":" + label;
    var hashBytes = hashFunction
        .hashString(edgeString, StandardCharsets.UTF_8)
        .asBytes();
    return EdgeId.create(hashBytes);
  }
}
