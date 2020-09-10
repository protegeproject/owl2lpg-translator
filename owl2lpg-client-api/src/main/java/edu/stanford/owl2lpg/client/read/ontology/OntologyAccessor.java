package edu.stanford.owl2lpg.client.read.ontology;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface OntologyAccessor {

  @Nonnull
  OWLOntologyID getOntologyId(@Nonnull ProjectId projectId,
                              @Nonnull BranchId branchId,
                              @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLAnnotation> getOntologyAnnotations(@Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                             @Nonnull BranchId branchId,
                             @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  <E extends OWLAxiom> Set<E> getAxiomsByType(@Nonnull AxiomType<E> axiomType,
                                              @Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLEntity> getAllEntities(@Nonnull ProjectId projectId,
                                @Nonnull BranchId branchId,
                                @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  <E extends OWLEntity> Set<E> getEntitiesByType(@Nonnull EntityType<E> type,
                                                 @Nonnull ProjectId projectId,
                                                 @Nonnull BranchId branchId,
                                                 @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLEntity> getEntitiesByIri(@Nonnull IRI iri,
                                  @Nonnull ProjectId projectId,
                                  @Nonnull BranchId branchId,
                                  @Nonnull OntologyDocumentId ontoDocId);
}
