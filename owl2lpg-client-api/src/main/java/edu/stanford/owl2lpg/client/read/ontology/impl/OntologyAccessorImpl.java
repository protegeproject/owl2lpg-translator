package edu.stanford.owl2lpg.client.read.ontology.impl;

import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.ontology.OntologyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyAccessorImpl implements OntologyAccessor {

  private static final String ONTOLOGY_INFO_QUERY_FILE = "ontology/ontology-info.cpy";
  private static final String ONTOLOGY_INFO_QUERY = read(ONTOLOGY_INFO_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final OntologyAnnotationsAccessor ontologyAnnotationsAccessor;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Inject
  public OntologyAccessorImpl(@Nonnull Driver driver,
                              @Nonnull OntologyAnnotationsAccessor ontologyAnnotationsAccessor,
                              @Nonnull AxiomAccessor axiomAccessor,
                              @Nonnull EntityAccessor entityAccessor) {
    this.driver = checkNotNull(driver);
    this.ontologyAnnotationsAccessor = checkNotNull(ontologyAnnotationsAccessor);
    this.axiomAccessor = checkNotNull(axiomAccessor);
    this.entityAccessor = checkNotNull(entityAccessor);
  }

  @Nonnull
  @Override
  public IRI getOntologyIri(@Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId ontoDocId) {
    return getProjectInfo("ontologyIri", projectId, branchId, ontoDocId)
        .map(IRI::create)
        .orElseThrow(RuntimeException::new);
  }

  @Nonnull
  @Override
  public Optional<IRI> getVersionIri(@Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId) {
    return getProjectInfo("versionIri", projectId, branchId, ontoDocId).map(IRI::create);
  }

  @Nonnull
  private Optional<String> getProjectInfo(@Nonnull String variable,
                                          @Nonnull ProjectId projectId,
                                          @Nonnull BranchId branchId,
                                          @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = Parameters.forContext(projectId, branchId, ontoDocId);
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        String outputString = null;
        var result = tx.run(ONTOLOGY_INFO_QUERY, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals(variable)) {
              outputString = (String) column.getValue();
            }
          }
        }
        return Optional.ofNullable(outputString);
      });
    }
  }

  @Nonnull
  @Override
  public Set<OWLAnnotation> getOntologyAnnotations(@Nonnull ProjectId projectId,
                                                   @Nonnull BranchId branchId,
                                                   @Nonnull OntologyDocumentId ontoDocId) {
    return ontologyAnnotationsAccessor.getOntologyAnnotations(projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public Set<OWLAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId) {
    return axiomAccessor.getAllAxioms(projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public <E extends OWLAxiom> Set<E> getAxiomsByType(@Nonnull AxiomType<E> axiomType,
                                                     @Nonnull ProjectId projectId,
                                                     @Nonnull BranchId branchId,
                                                     @Nonnull OntologyDocumentId ontoDocId) {
    return axiomAccessor.getAxiomsByType(axiomType, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public Set<OWLEntity> getAllEntities(@Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId,
                                       @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getAllEntities(projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public <E extends OWLEntity> Set<E> getEntitiesByType(@Nonnull EntityType<E> entityType,
                                                        @Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getEntitiesByType(entityType, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public Set<OWLEntity> getEntitiesByIri(@Nonnull IRI iri,
                                         @Nonnull ProjectId projectId,
                                         @Nonnull BranchId branchId,
                                         @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getEntitiesByIri(iri, projectId, branchId, ontoDocId);
  }
}
