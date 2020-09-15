package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ObjectPropertyRangeAxiomsIndex;
import edu.stanford.owl2lpg.client.read.axiom.RangeAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jObjectPropertyRangeAxiomsIndex implements ObjectPropertyRangeAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final RangeAxiomAccessor rangeAxiomAccessor;

  @Inject
  public Neo4jObjectPropertyRangeAxiomsIndex(@Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull OntologyDocumentId ontoDocId,
                                             @Nonnull RangeAxiomAccessor rangeAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.rangeAxiomAccessor = checkNotNull(rangeAxiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(@Nonnull OWLObjectProperty owlObjectProperty,
                                                                          @Nonnull OWLOntologyID owlOntologyID) {
    return rangeAxiomAccessor.getObjectPropertyRangeAxioms(owlObjectProperty, projectId, branchId, ontoDocId).stream();
  }
}
