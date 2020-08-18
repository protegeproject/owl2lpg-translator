package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface HierarchyAxiomBySubjectAccessor {

  Set<OWLSubClassOfAxiom> getSubClassOfAxiomsBySubClass(OWLClass subClass, AxiomContext context);

  Set<OWLSubObjectPropertyOfAxiom> getSubObjectPropertyOfAxiomsBySubProperty(OWLObjectProperty subProperty, AxiomContext context);

  Set<OWLSubDataPropertyOfAxiom> getSubDataPropertyOfAxiomsBySubProperty(OWLDataProperty subProperty, AxiomContext context);
}
