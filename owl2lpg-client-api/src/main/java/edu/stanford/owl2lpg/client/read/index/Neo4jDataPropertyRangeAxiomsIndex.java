package edu.stanford.owl2lpg.client.read.index;

import edu.stanford.bmir.protege.web.server.index.DataPropertyRangeAxiomsIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.RangeAxiomAccessor;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jDataPropertyRangeAxiomsIndex implements DataPropertyRangeAxiomsIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final RangeAxiomAccessor rangeAxiomAccessor;

  @Inject
  public Neo4jDataPropertyRangeAxiomsIndex(@Nonnull AxiomContext axiomContext,
                                           @Nonnull RangeAxiomAccessor rangeAxiomAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.rangeAxiomAccessor = checkNotNull(rangeAxiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(@Nonnull OWLDataProperty owlDataProperty,
                                                                      @Nonnull OWLOntologyID owlOntologyID) {
    return rangeAxiomAccessor.getDataPropertyRangeAxioms(owlDataProperty, axiomContext).stream();
  }
}
