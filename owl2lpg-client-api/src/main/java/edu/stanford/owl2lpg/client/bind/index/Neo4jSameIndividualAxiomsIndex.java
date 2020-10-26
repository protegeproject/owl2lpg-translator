package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.SameIndividualAxiomsIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSameIndividualAxiomsIndex implements SameIndividualAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public Neo4jSameIndividualAxiomsIndex(@Nonnull ProjectId projectId,
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
  public Stream<OWLSameIndividualAxiom> getSameIndividualAxioms(@Nonnull OWLIndividual owlIndividual,
                                                                @Nonnull OWLOntologyID ontologyId) {
    // TODO Handle the case when the instance is anonymous
    var documentId = documentIdMap.get(projectId, ontologyId);
    return (owlIndividual.isNamed()) ?
        axiomAccessor.getAxiomsBySubject(owlIndividual.asOWLNamedIndividual(), projectId, branchId, documentId)
            .stream()
            .filter(OWLSameIndividualAxiom.class::isInstance)
            .map(OWLSameIndividualAxiom.class::cast) :
        Stream.empty();
  }
}
