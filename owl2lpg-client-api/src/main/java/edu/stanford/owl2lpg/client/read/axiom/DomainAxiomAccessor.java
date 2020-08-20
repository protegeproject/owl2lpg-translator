package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface DomainAxiomAccessor {

  @Nonnull
  Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectProperty owlObjectProperty, AxiomContext context);

  @Nonnull
  Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty owlDataProperty, AxiomContext context);

  @Nonnull
  Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(OWLAnnotationProperty owlAnnotationProperty, AxiomContext context);
}
