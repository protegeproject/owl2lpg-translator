package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.AxiomsByTypeIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomByTypeAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAxiomsByTypeIndex implements AxiomsByTypeIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AxiomByTypeAccessor axiomByTypeAccessor;

  @Inject
  public Neo4jAxiomsByTypeIndex(@Nonnull AxiomContext axiomContext,
                                @Nonnull AxiomByTypeAccessor axiomByTypeAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.axiomByTypeAccessor = checkNotNull(axiomByTypeAccessor);
  }

  @Override
  public <T extends OWLAxiom> Stream<T> getAxiomsByType(@Nonnull AxiomType<T> axiomType,
                                                        @Nonnull OWLOntologyID owlOntologyID) {
    return axiomByTypeAccessor.getAxiomsByType(axiomType,
        axiomContext.getProjectId(),
        axiomContext.getBranchId(),
        axiomContext.getOntologyDocumentId()).stream();
  }
}
