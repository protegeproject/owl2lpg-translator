package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.index.AnnotationAssertionAxiomsIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
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
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AssertionAxiomAccessor assertionAxiomAccessor;

  @Inject
  public Neo4jAnnotationAssertionAxiomsIndex(@Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull DocumentIdMap documentIdMap,
                                             @Nonnull AssertionAxiomAccessor assertionAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.assertionAxiomAccessor = checkNotNull(assertionAxiomAccessor);
  }

  @Override
  @Nonnull
  public Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(@Nonnull IRI iri) {
    return getAnnotationAssertionsForSubject(iri).stream();
  }

  @Nonnull
  private Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionsForSubject(@Nonnull IRI iri) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> assertionAxiomAccessor.getAnnotationAssertionsBySubject(
            iri, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  @Nonnull
  public Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(@Nonnull IRI iri,
                                                                          @Nonnull OWLAnnotationProperty owlAnnotationProperty) {
    return getAnnotationAssertionsForSubjectAndProperty(iri, owlAnnotationProperty).stream();
  }

  @Nonnull
  private Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionsForSubjectAndProperty(@Nonnull IRI iri,
                                                                                        @Nonnull OWLAnnotationProperty owlAnnotationProperty) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> assertionAxiomAccessor.getAnnotationAssertionsBySubject(
            iri, owlAnnotationProperty, projectId, branchId, documentId).stream())
        .collect(ImmutableSet.toImmutableSet());
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
