package edu.stanford.owl2lpg.client.read.axiom.impl;

import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.client.read.axiom.RangeAxiomAccessor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_PROPERTY_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_RANGE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RangeAxiomAccessorImpl implements RangeAxiomAccessor {

  private static final String OBJECT_PROPERTY_RANGE_AXIOM_QUERY_FILE = "axioms/object-property-range-axiom.cpy";
  private static final String DATA_PROPERTY_RANGE_AXIOM_QUERY_FILE = "axioms/data-property-range-axiom.cpy";
  private static final String ANNOTATION_PROPERTY_RANGE_AXIOM_QUERY_FILE = "axioms/annotation-property-range-axiom.cpy";

  private static final String OBJECT_PROPERTY_RANGE_AXIOM_QUERY = read(OBJECT_PROPERTY_RANGE_AXIOM_QUERY_FILE);
  private static final String DATA_PROPERTY_RANGE_AXIOM_QUERY = read(DATA_PROPERTY_RANGE_AXIOM_QUERY_FILE);
  private static final String ANNOTATION_PROPERTY_RANGE_AXIOM_QUERY = read(ANNOTATION_PROPERTY_RANGE_AXIOM_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public RangeAxiomAccessorImpl(@Nonnull Driver driver,
                                @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    var nodeIndex = getNodeIndex(OBJECT_PROPERTY_RANGE_AXIOM_QUERY, owlObjectProperty, context);
    return collectObjectPropertyRangeAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private Set<OWLObjectPropertyRangeAxiom> collectObjectPropertyRangeAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(OBJECT_PROPERTY_RANGE.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLObjectPropertyRangeAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(OWLDataProperty owlDataProperty, AxiomContext context) {
    var nodeIndex = getNodeIndex(DATA_PROPERTY_RANGE_AXIOM_QUERY, owlDataProperty, context);
    return collectDataPropertyRangeAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private Set<OWLDataPropertyRangeAxiom> collectDataPropertyRangeAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(DATA_PROPERTY_RANGE.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLDataPropertyRangeAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    var nodeIndex = getNodeIndex(ANNOTATION_PROPERTY_RANGE_AXIOM_QUERY, owlAnnotationProperty, context);
    return collectAnnotationPropertyRangeAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private Set<OWLAnnotationPropertyRangeAxiom> collectAnnotationPropertyRangeAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(ANNOTATION_PROPERTY_RANGE.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotationPropertyRangeAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  private NodeIndex getNodeIndex(String queryString, OWLEntity entity, AxiomContext context) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var inputParams = createInputParams(entity, context);
        var result = tx.run(queryString, inputParams);
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
  private static Value createInputParams(OWLEntity entity, AxiomContext context) {
    return Parameters.forEntityIri(entity.getIRI(), context.getProjectId(), context.getBranchId(), context.getOntologyDocumentId());
  }
}
