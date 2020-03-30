package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.translator.Translator;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collection;

/**
 * The translator to translate the OWL 2 constructs to labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TranslatorImpl implements Translator {

  @Override
  public Collection<Graph> translate(OWLOntology ontology) {
    return null;
  }

  @Override
  public Graph translate(OWLAxiom axiom) {
    return axiom.accept(new AxiomTranslator());
  }
}
