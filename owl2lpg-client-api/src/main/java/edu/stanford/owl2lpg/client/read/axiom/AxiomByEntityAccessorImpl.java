package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.client.read.Parameters;
import org.neo4j.driver.Driver;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomByEntityAccessorImpl implements AxiomByEntityAccessor {

  private static final String SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY_FILE =
      "axioms/sub-class-of-axiom-by-sub-class.cpy";
  private static final String SUB_OBJECT_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY_FILE =
      "axioms/sub-object-property-of-axiom-by-sub-property.cpy";

  private static final String SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY =
      read(SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY_FILE);
  private static final String SUB_OBJECT_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY =
      read(SUB_OBJECT_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AxiomByEntityAccessorImpl(@Nonnull Driver driver,
                                   @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Override
  public Set<OWLSubClassOfAxiom> getSubClassOfAxiomsBySubClass(AxiomContext context, OWLClass subClass) {
    var nodeIndex = getNodeIndex(context, subClass, SUB_CLASS_OF_AXIOMS_BY_SUB_CLASS_QUERY);
    return collectSubClassOfAxiomsFromIndex(nodeIndex);
  }

  @Override
  public Set<OWLSubObjectPropertyOfAxiom> getSubObjectPropertyOfAxiomsBySubProperty(AxiomContext context, OWLObjectProperty subProperty) {
    var nodeIndex = getNodeIndex(context, subProperty, SUB_OBJECT_PROPERTY_OF_AXIOMS_BY_SUB_PROPERTY_QUERY);
    return collectSubObjectPropertyOfAxiomsFromIndex(nodeIndex);
  }

  private NodeIndex getNodeIndex(AxiomContext context, OWLEntity entity, String queryString) {
    try (var session = driver.session()) {
      var args = Parameters.forEntity(context, entity);
      return session.readTransaction(tx -> {
        var result = tx.run(queryString, args);
        var nodeIndexBuilder = new NodeIndexImpl.Builder();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                path.spliterator().forEachRemaining(nodeIndexBuilder::add);
              }
            }
          }
        }
        return nodeIndexBuilder.build();
      });
    }
  }

  @Nonnull
  private Set<OWLSubClassOfAxiom> collectSubClassOfAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLSubClassOfAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  private Set<OWLSubObjectPropertyOfAxiom> collectSubObjectPropertyOfAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLSubObjectPropertyOfAxiom.class))
        .collect(Collectors.toSet());
  }
}
