package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.model.GraphFactory.withIdentifierFrom;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.NODE_ID;

/**
 * A visitor that contains the implementation to translate the OWL 2 individuals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IndividualVisitor implements OWLIndividualVisitorEx<Translation> {

  @Nonnull
  private final OWLEntityVisitorEx<Translation> entityVisitor;

  @Inject
  public IndividualVisitor(@Nonnull OWLEntityVisitorEx<Translation> entityVisitor) {
    this.entityVisitor = checkNotNull(entityVisitor);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual individual) {
    return entityVisitor.visit(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    var anonymousNode = Node(NodeLabels.ANONYMOUS_INDIVIDUAL,
        Properties(NODE_ID, individual.getID().toString()),
        withIdentifierFrom(individual));
    return Translation.create(anonymousNode, ImmutableList.of(), ImmutableList.of());
  }
}
