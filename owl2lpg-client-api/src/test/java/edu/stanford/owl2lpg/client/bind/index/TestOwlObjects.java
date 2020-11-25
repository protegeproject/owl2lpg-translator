package edu.stanford.owl2lpg.client.bind.index;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.AnnotationProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Datatype;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Literal;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectProperty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TestOwlObjects {

  static final IRI iriA = IRI("http://example.org/A");
  static final IRI iriB = IRI("http://example.org/B");
  static final IRI iriC = IRI("http://example.org/C");
  static final IRI iriP = IRI("http://example.org/P");
  static final IRI iriQ = IRI("http://example.org/Q");
  static final IRI iriR = IRI("http://example.org/R");

  static final OWLClass clsA = Class(iriA);
  static final OWLClass clsB = Class(iriB);
  static final OWLClass clsC = Class(iriC);

  static final OWLDataProperty dpP = DataProperty(iriP);
  static final OWLDataProperty dpQ = DataProperty(iriQ);
  static final OWLDataProperty dpR = DataProperty(iriR);

  static final OWLObjectProperty opP = ObjectProperty(iriP);
  static final OWLObjectProperty opQ = ObjectProperty(iriQ);
  static final OWLObjectProperty opR = ObjectProperty(iriR);

  static final OWLAnnotationProperty apP = AnnotationProperty(iriP);
  static final OWLAnnotationProperty apQ = AnnotationProperty(iriQ);
  static final OWLAnnotationProperty apR = AnnotationProperty(iriR);

  static final OWLLiteral litStrA = Literal("A");
  static final OWLLiteral litStrB = Literal("B");
  static final OWLLiteral litStrC = Literal("C");

  static final OWLLiteral litLangA = Literal("A", "en");
  static final OWLLiteral litLangB = Literal("B", "en");
  static final OWLLiteral litLangC = Literal("C", "id");

  static final OWLLiteral litInt = Literal(1);
  static final OWLLiteral litBool = Literal(true);
  static final OWLLiteral litCustom = Literal("X", Datatype(IRI("http://example.org/datatype")));
}
