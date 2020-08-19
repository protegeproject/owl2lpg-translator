package edu.stanford.owl2lpg.client.read.index;

import edu.stanford.bmir.protege.web.server.index.AnnotationAssertionAxiomsBySubjectIndex;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAnnotationAssertionAxiomsBySubjectIndex implements AnnotationAssertionAxiomsBySubjectIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor;

  @Inject
  public Neo4jAnnotationAssertionAxiomsBySubjectIndex(@Nonnull AxiomContext axiomContext,
                                                      @Nonnull AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.assertionAxiomBySubjectAccessor = checkNotNull(assertionAxiomBySubjectAccessor);
  }

  @Override
  public Stream<OWLAnnotationAssertionAxiom> getAxiomsForSubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                                                 @Nonnull OWLOntologyID owlOntologyID) {
    return assertionAxiomBySubjectAccessor.getAnnotationAssertionsForSubject(owlAnnotationSubject, axiomContext);
  }
}
