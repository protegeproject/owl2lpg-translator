package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.AnnotationPropertyRangeAxiomsIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.RangeAxiomAccessor;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAnnotationPropertyRangeAxiomsIndex implements AnnotationPropertyRangeAxiomsIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final RangeAxiomAccessor rangeAxiomAccessor;

  @Inject
  public Neo4jAnnotationPropertyRangeAxiomsIndex(@Nonnull AxiomContext axiomContext,
                                                 @Nonnull RangeAxiomAccessor rangeAxiomAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.rangeAxiomAccessor = checkNotNull(rangeAxiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                                                                  @Nonnull OWLOntologyID owlOntologyID) {
    return rangeAxiomAccessor.getAnnotationPropertyRangeAxioms(owlAnnotationProperty, axiomContext).stream();
  }
}
