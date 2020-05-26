package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.EntityVisitor;
import edu.stanford.owl2lpg.translator.visitors.OntologyVisitor;
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
  private final Provider<OntologyVisitor> visitor;

  @Inject
  public OntologyTranslator(@Nonnull Provider<OntologyVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLOntology ontology) {
    checkNotNull(ontology);
    return ontology.accept(visitor.get());
  }
}
