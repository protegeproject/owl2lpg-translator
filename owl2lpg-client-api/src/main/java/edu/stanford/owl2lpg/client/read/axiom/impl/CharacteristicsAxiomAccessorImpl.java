package edu.stanford.owl2lpg.client.read.axiom.impl;

import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ASYMMETRIC_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.FUNCTIONAL_DATA_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.FUNCTIONAL_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.REFLEXIVE_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SYMMETRIC_OBJECT_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.TRANSITIVE_OBJECT_PROPERTY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CharacteristicsAxiomAccessorImpl implements CharacteristicsAxiomAccessor {

  private static final String OBJECT_PROPERTY_CHARACTERISTICS_QUERY_FILE = "axioms/object-property-characteristics-axiom.cpy";
  private static final String DATA_PROPERTY_CHARACTERISTICS_QUERY_FILE = "axioms/data-property-characteristics-axiom.cpy";

  private static final String OBJECT_PROPERTY_CHARACTERISTICS_QUERY = read(OBJECT_PROPERTY_CHARACTERISTICS_QUERY_FILE);
  private static final String DATA_PROPERTY_CHARACTERISTICS_QUERY = read(DATA_PROPERTY_CHARACTERISTICS_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Inject
  public CharacteristicsAxiomAccessorImpl(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  @Override
  public boolean isFunctional(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(OBJECT_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, FUNCTIONAL_OBJECT_PROPERTY, projectId, branchId, ontoDocId));
  }

  @Override
  public boolean isInverseFunctional(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(OBJECT_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, INVERSE_FUNCTIONAL_OBJECT_PROPERTY, projectId, branchId, ontoDocId));
  }

  @Override
  public boolean isTransitive(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(OBJECT_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, TRANSITIVE_OBJECT_PROPERTY, projectId, branchId, ontoDocId));
  }

  @Override
  public boolean isSymmetric(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(OBJECT_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, SYMMETRIC_OBJECT_PROPERTY, projectId, branchId, ontoDocId));
  }

  @Override
  public boolean isAsymmetric(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(OBJECT_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, ASYMMETRIC_OBJECT_PROPERTY, projectId, branchId, ontoDocId));
  }

  @Override
  public boolean isReflexive(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(OBJECT_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, REFLEXIVE_OBJECT_PROPERTY, projectId, branchId, ontoDocId));
  }

  @Override
  public boolean isIrreflexive(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(OBJECT_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, IRREFLEXIVE_OBJECT_PROPERTY, projectId, branchId, ontoDocId));
  }

  @Override
  public boolean isFunctional(OWLDataProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(DATA_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, FUNCTIONAL_DATA_PROPERTY, projectId, branchId, ontoDocId));
  }

  @Nonnull
  private static Value getInputParams(OWLProperty property, NodeLabels nodeLabels, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forPropertyWithCharacteristicType(property.getIRI(), nodeLabels.getMainLabel(), projectId, branchId, ontoDocId);
  }

  private boolean hasResult(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> tx.run(queryString, inputParams).list().isEmpty());
    }
  }
}
