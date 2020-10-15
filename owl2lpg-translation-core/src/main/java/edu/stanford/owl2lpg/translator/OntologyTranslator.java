package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A translator to translate the OWL 2 ontology.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyTranslator {

  @Nonnull
  private final Provider<OWLNamedObjectVisitorEx<Translation>> visitor;

  @Inject
  public OntologyTranslator(@Nonnull Provider<OWLNamedObjectVisitorEx<Translation>> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLOntology ontology) {
    checkNotNull(ontology);
    return ontology.accept(visitor.get());
  }
}
