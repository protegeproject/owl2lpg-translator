package edu.stanford.owl2lpg.client.read.index;

import edu.stanford.bmir.protege.web.server.index.SubObjectPropertyAxiomsBySubPropertyIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomByEntityAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSubObjectPropertyAxiomsBySubPropertyIndex
    implements SubObjectPropertyAxiomsBySubPropertyIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final AxiomByEntityAccessor axiomByEntityAccessor;

  @Inject
  public Neo4jSubObjectPropertyAxiomsBySubPropertyIndex(@Nonnull AxiomContext axiomContext,
                                                        @Nonnull AxiomByEntityAccessor axiomByEntityAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.axiomByEntityAccessor = checkNotNull(axiomByEntityAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLSubObjectPropertyOfAxiom> getSubPropertyOfAxioms(@Nonnull OWLObjectProperty subProperty,
                                                                    @Nonnull OWLOntologyID owlOntologyID) {
    return axiomByEntityAccessor.getSubObjectPropertyOfAxiomsBySubProperty(axiomContext, subProperty).stream();
  }
}
