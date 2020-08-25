package edu.stanford.owl2lpg.client.read.axiom.impl;

import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.DomainAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_PROPERTY_DOMAIN;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_DOMAIN;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_DOMAIN;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DomainAxiomAccessorImpl implements DomainAxiomAccessor {

  private static final String OBJECT_PROPERTY_DOMAIN_AXIOM_QUERY_FILE = "axioms/object-property-domain-axiom.cpy";
  private static final String DATA_PROPERTY_DOMAIN_AXIOM_QUERY_FILE = "axioms/data-property-domain-axiom.cpy";
  private static final String ANNOTATION_PROPERTY_DOMAIN_AXIOM_QUERY_FILE = "axioms/annotation-property-domain-axiom.cpy";

  private static final String OBJECT_PROPERTY_DOMAIN_AXIOM_QUERY = read(OBJECT_PROPERTY_DOMAIN_AXIOM_QUERY_FILE);
  private static final String DATA_PROPERTY_DOMAIN_AXIOM_QUERY = read(DATA_PROPERTY_DOMAIN_AXIOM_QUERY_FILE);
  private static final String ANNOTATION_PROPERTY_DOMAIN_AXIOM_QUERY = read(ANNOTATION_PROPERTY_DOMAIN_AXIOM_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public DomainAxiomAccessorImpl(@Nonnull Driver driver,
                                 @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectProperty owlObjectProperty, AxiomContext context) {
    var nodeIndex = getNodeIndex(OBJECT_PROPERTY_DOMAIN_AXIOM_QUERY, owlObjectProperty, context);
    return collectObjectPropertyDomainAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private Set<OWLObjectPropertyDomainAxiom> collectObjectPropertyDomainAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(OBJECT_PROPERTY_DOMAIN.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLObjectPropertyDomainAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty owlDataProperty, AxiomContext context) {
    var nodeIndex = getNodeIndex(DATA_PROPERTY_DOMAIN_AXIOM_QUERY, owlDataProperty, context);
    return collectDataPropertyDomainAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private Set<OWLDataPropertyDomainAxiom> collectDataPropertyDomainAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(DATA_PROPERTY_DOMAIN.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLDataPropertyDomainAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context) {
    var nodeIndex = getNodeIndex(ANNOTATION_PROPERTY_DOMAIN_AXIOM_QUERY, owlAnnotationProperty, context);
    return collectAnnotationPropertyDomainAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private Set<OWLAnnotationPropertyDomainAxiom> collectAnnotationPropertyDomainAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(ANNOTATION_PROPERTY_DOMAIN.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotationPropertyDomainAxiom.class))
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
