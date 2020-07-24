package edu.stanford.owl2lpg.client.read.index;

import edu.stanford.bmir.protege.web.server.index.SubClassOfAxiomsBySubClassIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomByEntityAccessor;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSubClassOfAxiomsBySubClassIndex implements SubClassOfAxiomsBySubClassIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AxiomByEntityAccessor axiomByEntityAccessor;

  @Inject
  public Neo4jSubClassOfAxiomsBySubClassIndex(@Nonnull AxiomContext axiomContext,
                                              @Nonnull AxiomByEntityAccessor axiomByEntityAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.axiomByEntityAccessor = checkNotNull(axiomByEntityAccessor);
  }

  @Override
  public Stream<OWLSubClassOfAxiom> getSubClassOfAxiomsForSubClass(@Nonnull OWLClass subClass,
                                                                   @Nonnull OWLOntologyID owlOntologyID) {
    return axiomByEntityAccessor.getSubClassOfAxiomsBySubClass(axiomContext, subClass)
        .stream()
        .map(OWLSubClassOfAxiom.class::cast);
  }
}