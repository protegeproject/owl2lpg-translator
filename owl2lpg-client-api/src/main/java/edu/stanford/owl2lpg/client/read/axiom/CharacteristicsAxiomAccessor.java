package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface CharacteristicsAxiomAccessor {

  boolean isFunctional(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId);

  boolean isInverseFunctional(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId);

  boolean isTransitive(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId);

  boolean isSymmetric(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId);

  boolean isAsymmetric(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId);

  boolean isReflexive(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId);

  boolean isIrreflexive(OWLObjectProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId);

  boolean isFunctional(OWLDataProperty property, ProjectId projectId, BranchId branchId, OntologyDocumentId ontoDocId);
}
