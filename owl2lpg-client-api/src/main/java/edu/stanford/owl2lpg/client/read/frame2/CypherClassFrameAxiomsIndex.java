package edu.stanford.owl2lpg.client.read.frame2;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.index.ClassFrameAxiomsIndex;
import edu.stanford.owl2lpg.client.read.axiom.FrameAxiomAccessor;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.server.index.ClassFrameAxiomsIndex.AnnotationsTreatment.EXCLUDE_ANNOTATIONS;
import static edu.stanford.bmir.protege.web.server.index.ClassFrameAxiomsIndex.AnnotationsTreatment.INCLUDE_ANNOTATIONS;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CypherClassFrameAxiomsIndex implements ClassFrameAxiomsIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final FrameAxiomAccessor frameAxiomAccessor;

  @Inject
  public CypherClassFrameAxiomsIndex(@Nonnull AxiomContext axiomContext,
                                     @Nonnull FrameAxiomAccessor frameAxiomAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.frameAxiomAccessor = checkNotNull(frameAxiomAccessor);
  }

  @Override
  public Set<OWLAxiom> getFrameAxioms(OWLClass owlClass, AnnotationsTreatment annotationsTreatment) {
    return frameAxiomAccessor.getFrameAxioms(axiomContext, owlClass)
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

  @Override
  public Stream<OWLSubClassOfAxiom> getFrameSubClassOfAxioms(OWLClass owlClass) {
    return getFrameAxioms(owlClass, INCLUDE_ANNOTATIONS)
        .stream()
        .filter(OWLSubClassOfAxiom.class::isInstance)
        .map(OWLSubClassOfAxiom.class::cast);
  }
}
