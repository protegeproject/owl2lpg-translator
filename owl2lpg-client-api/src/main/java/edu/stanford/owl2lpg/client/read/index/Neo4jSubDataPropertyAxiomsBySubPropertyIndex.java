package edu.stanford.owl2lpg.client.read.index;

import edu.stanford.bmir.protege.web.server.index.SubDataPropertyAxiomsBySubPropertyIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomByEntityAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSubDataPropertyAxiomsBySubPropertyIndex
    implements SubDataPropertyAxiomsBySubPropertyIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AxiomByEntityAccessor axiomByEntityAccessor;

  @Inject
  public Neo4jSubDataPropertyAxiomsBySubPropertyIndex(@Nonnull AxiomContext axiomContext,
                                                      @Nonnull AxiomByEntityAccessor axiomByEntityAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.axiomByEntityAccessor = checkNotNull(axiomByEntityAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLSubDataPropertyOfAxiom> getSubPropertyOfAxioms(@Nonnull OWLDataProperty subProperty,
                                                                  @Nonnull OWLOntologyID owlOntologyID) {
    return axiomByEntityAccessor.getSubDataPropertyOfAxiomsBySubProperty(axiomContext, subProperty).stream();
  }
}
