package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.InverseObjectPropertyAxiomsIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jInverseObjectPropertyAxiomsIndex implements InverseObjectPropertyAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jInverseObjectPropertyAxiomsIndex(@Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull DocumentIdMap documentIdMap,
                                               @Nonnull AxiomAccessor axiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(@Nonnull OWLObjectProperty owlObjectProperty,
                                                                                @Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    return axiomAccessor.getAxiomsBySubject(owlObjectProperty, projectId, branchId, documentId)
        .stream()
        .filter(OWLInverseObjectPropertiesAxiom.class::isInstance)
        .map(OWLInverseObjectPropertiesAxiom.class::cast);
  }
}
