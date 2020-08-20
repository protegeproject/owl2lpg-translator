package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface RangeAxiomAccessor {

  @Nonnull
  Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(OWLObjectProperty owlObjectProperty, AxiomContext context);

  @Nonnull
  Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(OWLDataProperty owlDataProperty, AxiomContext context);

  @Nonnull
  Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context);
}
