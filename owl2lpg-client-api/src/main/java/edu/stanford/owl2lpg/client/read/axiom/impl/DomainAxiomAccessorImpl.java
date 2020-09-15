package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.DomainAxiomAccessor;
import edu.stanford.owl2lpg.client.read.impl.NodeIndexImpl;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
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
  public ImmutableSet<OWLObjectPropertyDomainAxiom>
  getObjectPropertyDomainAxioms(@Nonnull OWLObjectProperty owlObjectProperty,
                                @Nonnull ProjectId projectId,
                                @Nonnull BranchId branchId,
                                @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(owlObjectProperty, projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(OBJECT_PROPERTY_DOMAIN_AXIOM_QUERY, inputParams);
    return collectObjectPropertyDomainAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLObjectPropertyDomainAxiom> collectObjectPropertyDomainAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(OBJECT_PROPERTY_DOMAIN.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLObjectPropertyDomainAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLDataPropertyDomainAxiom>
  getDataPropertyDomainAxioms(@Nonnull OWLDataProperty owlDataProperty,
                              @Nonnull ProjectId projectId,
                              @Nonnull BranchId branchId,
                              @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(owlDataProperty, projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(DATA_PROPERTY_DOMAIN_AXIOM_QUERY, inputParams);
    return collectDataPropertyDomainAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLDataPropertyDomainAxiom> collectDataPropertyDomainAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(DATA_PROPERTY_DOMAIN.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLDataPropertyDomainAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationPropertyDomainAxiom>
  getAnnotationPropertyDomainAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = createInputParams(owlAnnotationProperty, projectId, branchId, ontoDocId);
    var nodeIndex = getNodeIndex(ANNOTATION_PROPERTY_DOMAIN_AXIOM_QUERY, inputParams);
    return collectAnnotationPropertyDomainAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLAnnotationPropertyDomainAxiom> collectAnnotationPropertyDomainAxiomsFromIndex(NodeIndex nodeIndex) {
    return nodeIndex.getNodes(ANNOTATION_PROPERTY_DOMAIN.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotationPropertyDomainAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private NodeIndex getNodeIndex(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
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
  private static Value createInputParams(OWLEntity entity, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forEntityIri(entity.getIRI(), projectId, branchId, ontoDocId);
  }
}
