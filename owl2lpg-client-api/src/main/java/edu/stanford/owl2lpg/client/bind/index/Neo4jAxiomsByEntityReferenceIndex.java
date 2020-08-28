package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.AxiomsByEntityReferenceIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAxiomsByEntityReferenceIndex implements AxiomsByEntityReferenceIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AxiomBySubjectAccessor axiomBySubjectAccessor;

  @Inject
  public Neo4jAxiomsByEntityReferenceIndex(@Nonnull AxiomContext axiomContext,
                                           @Nonnull AxiomBySubjectAccessor axiomBySubjectAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.axiomBySubjectAccessor = checkNotNull(axiomBySubjectAccessor);
  }

  @Override
  public Stream<OWLAxiom> getReferencingAxioms(@Nonnull OWLEntity owlEntity,
                                               @Nonnull OWLOntologyID owlOntologyID) {
    return axiomBySubjectAccessor.getAxiomsForSubject(owlEntity, axiomContext).stream();
  }
}
