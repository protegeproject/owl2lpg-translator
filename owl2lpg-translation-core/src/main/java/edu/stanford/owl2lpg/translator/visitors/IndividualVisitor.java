package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
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

/**
 * A visitor that contains the implementation to translate the OWL 2 individuals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IndividualVisitor implements OWLIndividualVisitorEx<Translation> {

  @Nonnull
  private final OWLEntityVisitorEx<Translation> entityVisitor;

  private Node mainNode;

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
    mainNode = createMainNode(individual, NodeLabels.ANONYMOUS_INDIVIDUAL);
    return Translation.create(mainNode, ImmutableList.of(), ImmutableList.of());
  }

  @Nonnull
  protected Node createMainNode(@Nonnull OWLAnonymousIndividual individual,
                                @Nonnull ImmutableList<String> nodeLabels) {
    checkNotNull(individual);
    checkNotNull(nodeLabels);
    return Node(nodeLabels,
        Properties(PropertyNames.NODE_ID, String.valueOf(individual.getID())),
        withIdentifierFrom(individual));
  }
}
