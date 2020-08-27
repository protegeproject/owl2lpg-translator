package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ClassAssertionAxiomsByClassIndex;
import edu.stanford.owl2lpg.client.read.axiom.ClassAssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jClassAssertionAxiomsByClassIndex implements ClassAssertionAxiomsByClassIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final ClassAssertionAxiomAccessor assertionAxiomByTypeAccessor;

  @Inject
  public Neo4jClassAssertionAxiomsByClassIndex(@Nonnull AxiomContext axiomContext,
                                               @Nonnull ClassAssertionAxiomAccessor assertionAxiomByTypeAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.assertionAxiomByTypeAccessor = checkNotNull(assertionAxiomByTypeAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLClassAssertionAxiom> getClassAssertionAxioms(@Nonnull OWLClass owlClass,
                                                                @Nonnull OWLOntologyID owlOntologyID) {
    return assertionAxiomByTypeAccessor.getClassAssertions(owlClass, axiomContext).stream();
  }
}
