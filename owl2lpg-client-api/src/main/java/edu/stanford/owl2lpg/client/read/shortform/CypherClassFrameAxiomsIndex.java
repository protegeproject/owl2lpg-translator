package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.index.ClassFrameAxiomsIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomSubjectAccessor;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.server.index.ClassFrameAxiomsIndex.AnnotationsTreatment.EXCLUDE_ANNOTATIONS;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CypherClassFrameAxiomsIndex implements ClassFrameAxiomsIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AxiomSubjectAccessor axiomSubjectAccessor;

  @Inject
  public CypherClassFrameAxiomsIndex(@Nonnull AxiomContext axiomContext,
                                     @Nonnull AxiomSubjectAccessor axiomSubjectAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.axiomSubjectAccessor = checkNotNull(axiomSubjectAccessor);
  }

  @Override
  public Set<OWLAxiom> getFrameAxioms(OWLClass owlClass, AnnotationsTreatment annotationsTreatment) {
    return axiomSubjectAccessor.getAxiomSubject(axiomContext, owlClass)
        .stream()
        .filter(axiom -> {
          var accepted = true;
          if (annotationsTreatment.equals(EXCLUDE_ANNOTATIONS)) {
            accepted = !(axiom instanceof OWLAnnotationAssertionAxiom);
          }
          return accepted;
        })
        .collect(ImmutableSet.toImmutableSet());
  }
}
