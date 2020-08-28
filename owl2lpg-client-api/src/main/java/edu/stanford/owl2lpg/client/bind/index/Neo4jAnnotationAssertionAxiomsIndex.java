package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.AnnotationAssertionAxiomsIndex;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAnnotationAssertionAxiomsIndex implements AnnotationAssertionAxiomsIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor;

  @Inject
  public Neo4jAnnotationAssertionAxiomsIndex(@Nonnull AxiomContext axiomContext,
                                             @Nonnull AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.assertionAxiomBySubjectAccessor = checkNotNull(assertionAxiomBySubjectAccessor);
  }

  @Override
  @Nonnull
  public Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms() {
    return Stream.empty();
  }

  @Override
  @Nonnull
  public Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(@Nonnull IRI iri) {
    return getAnnotationAssertionsForSubject(iri).stream();
  }

  @Nonnull
  private Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionsForSubject(@Nonnull IRI iri) {
    return assertionAxiomBySubjectAccessor.getAnnotationAssertionsForSubject(iri, axiomContext);
  }

  @Override
  @Nonnull
  public Stream<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionAxioms(@Nonnull IRI iri, @Nonnull OWLAnnotationProperty owlAnnotationProperty) {
    return getAnnotationAssertionsForSubjectAndProperty(iri, owlAnnotationProperty).stream();
  }

  @Nonnull
  private Set<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsForSubjectAndProperty(@Nonnull IRI iri, @Nonnull OWLAnnotationProperty owlAnnotationProperty) {
    return assertionAxiomBySubjectAccessor.getAnnotationAssertionsForSubject(iri, owlAnnotationProperty, axiomContext);
  }

  @Override
  public long getAnnotationAssertionAxiomsCount(@Nonnull IRI iri) {
    return getAnnotationAssertionsForSubject(iri).size();
  }

  @Override
  public long getAnnotationAssertionAxiomsCount(@Nonnull IRI iri, @Nonnull OWLAnnotationProperty owlAnnotationProperty) {
    return getAnnotationAssertionsForSubjectAndProperty(iri, owlAnnotationProperty).size();
  }
}
