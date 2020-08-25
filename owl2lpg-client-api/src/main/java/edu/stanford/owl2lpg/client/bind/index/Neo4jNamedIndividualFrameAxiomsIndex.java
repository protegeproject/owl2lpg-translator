package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.NamedIndividualFrameAxiomIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jNamedIndividualFrameAxiomsIndex implements NamedIndividualFrameAxiomIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AxiomBySubjectAccessor axiomBySubjectAccessor;

  @Inject
  public Neo4jNamedIndividualFrameAxiomsIndex(@Nonnull AxiomContext axiomContext,
                                              @Nonnull AxiomBySubjectAccessor axiomBySubjectAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.axiomBySubjectAccessor = checkNotNull(axiomBySubjectAccessor);
  }

  @Nonnull
  @Override
  public Set<OWLAxiom> getNamedIndividualFrameAxioms(@Nonnull OWLNamedIndividual owlNamedIndividual) {
    return axiomBySubjectAccessor.getAxiomForSubject(owlNamedIndividual, axiomContext);
  }
}
