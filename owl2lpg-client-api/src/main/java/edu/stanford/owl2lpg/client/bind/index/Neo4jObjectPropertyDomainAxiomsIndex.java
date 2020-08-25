package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ObjectPropertyDomainAxiomsIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.DomainAxiomAccessor;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jObjectPropertyDomainAxiomsIndex implements ObjectPropertyDomainAxiomsIndex {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final DomainAxiomAccessor domainAxiomAccessor;

  @Inject
  public Neo4jObjectPropertyDomainAxiomsIndex(@Nonnull AxiomContext axiomContext,
                                              @Nonnull DomainAxiomAccessor domainAxiomAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.domainAxiomAccessor = checkNotNull(domainAxiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(@Nonnull OWLObjectProperty owlObjectProperty, @Nonnull OWLOntologyID owlOntologyID) {
    return domainAxiomAccessor.getObjectPropertyDomainAxioms(owlObjectProperty, axiomContext).stream();
  }
}
