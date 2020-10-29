package edu.stanford.owl2lpg.client.read.axiom.impl;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
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

  private static final String OBJECT_PROPERTY_CHARACTERISTICS_QUERY_FILE = "read/axioms/object-property-characteristics-axiom.cpy";
  private static final String DATA_PROPERTY_CHARACTERISTICS_QUERY_FILE = "read/axioms/data-property-characteristics-axiom.cpy";

  private static final String OBJECT_PROPERTY_CHARACTERISTICS_QUERY = read(OBJECT_PROPERTY_CHARACTERISTICS_QUERY_FILE);
  private static final String DATA_PROPERTY_CHARACTERISTICS_QUERY = read(DATA_PROPERTY_CHARACTERISTICS_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Inject
  public CharacteristicsAxiomAccessorImpl(@Nonnull GraphReader graphReader) {
    this.graphReader = checkNotNull(graphReader);
  }

  @Override
  public boolean isFunctional(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(FUNCTIONAL_OBJECT_PROPERTY, property, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isInverseFunctional(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(INVERSE_FUNCTIONAL_OBJECT_PROPERTY, property, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isTransitive(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(TRANSITIVE_OBJECT_PROPERTY, property, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isSymmetric(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(SYMMETRIC_OBJECT_PROPERTY, property, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isAsymmetric(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(ASYMMETRIC_OBJECT_PROPERTY, property, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isReflexive(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(REFLEXIVE_OBJECT_PROPERTY, property, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean isIrreflexive(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return hasResult(IRREFLEXIVE_OBJECT_PROPERTY, property, projectId, branchId, ontoDocId);
  }

  private boolean hasResult(NodeLabels propertyNodeLabels, OWLProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return graphReader.hasResult(OBJECT_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, propertyNodeLabels, projectId, branchId, ontoDocId));
  }

  @Override
  public boolean isFunctional(OWLDataProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return graphReader.hasResult(DATA_PROPERTY_CHARACTERISTICS_QUERY, getInputParams(property, FUNCTIONAL_DATA_PROPERTY, projectId, branchId, ontoDocId));
  }

  @Nonnull
  private static Value getInputParams(OWLProperty property, NodeLabels nodeLabels, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId) {
    return Parameters.forPropertyWithCharacteristicType(property.getIRI(), nodeLabels.getMainLabel(), projectId, branchId, ontoDocId);
  }
}
