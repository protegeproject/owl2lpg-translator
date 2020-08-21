package edu.stanford.owl2lpg.client.read.index;

import edu.stanford.bmir.protege.web.server.index.DataPropertyDomainAxiomsIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.DomainAxiomAccessor;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jDataPropertyDomainAxiomsIndex implements DataPropertyDomainAxiomsIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final DomainAxiomAccessor domainAxiomAccessor;

  @Inject
  public Neo4jDataPropertyDomainAxiomsIndex(@Nonnull AxiomContext axiomContext,
                                            @Nonnull DomainAxiomAccessor domainAxiomAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.domainAxiomAccessor = checkNotNull(domainAxiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(@Nonnull OWLDataProperty owlDataProperty,
                                                                        @Nonnull OWLOntologyID owlOntologyID) {
    return domainAxiomAccessor.getDataPropertyDomainAxioms(owlDataProperty, axiomContext).stream();
  }
}
