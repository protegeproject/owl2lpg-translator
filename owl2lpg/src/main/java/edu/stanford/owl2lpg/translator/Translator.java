package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collection;

/**
 * Represents the translator that will translate OWL 2 object constructs to
 * their corresponding labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface Translator {

  Collection<Graph> translate(OWLOntology ontology);

  Graph translate(OWLAxiom axiom);
}
