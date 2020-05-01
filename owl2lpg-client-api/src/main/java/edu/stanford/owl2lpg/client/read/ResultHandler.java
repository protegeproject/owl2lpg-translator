package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.TranslationBuilder;
import org.neo4j.driver.Result;
import org.neo4j.driver.internal.value.PathValue;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;
import java.util.stream.Collectors;

import static edu.stanford.owl2lpg.model.GraphFactory.Node;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ResultHandler {
  public Set<OWLAxiom> getAxioms(Result result) {
    var records = result.list();
    var translations = result.stream()
        .map(record -> {
          var translationBuilder = new TranslationBuilder();
          var value = record.get(0);
          if (value instanceof PathValue) {
            var path = value.asPath();
            path.forEach(segment -> {
              var startNode = segment.start();
              var fromNode = Node(
                  NodeId.create(startNode.id()),
                  ImmutableList.copyOf(startNode.labels()),
                  Properties.create(ImmutableMap.copyOf(startNode.asMap())));
              var endNode = segment.end();
              var toNode = Node(
                  NodeId.create(endNode.id()),
                  ImmutableList.copyOf(endNode.labels()),
                  Properties.create(ImmutableMap.copyOf(endNode.asMap())));
              var relationship = segment.relationship();
              var edgeLabel = relationship.type();
              var edgeProperties = Properties.create(ImmutableMap.copyOf(relationship.asMap()));
              translationBuilder.add(fromNode, toNode, edgeLabel, edgeProperties);
            });
          }
          return translationBuilder.build();
        })
        .collect(Collectors.toSet());
    return null;
  }
}
