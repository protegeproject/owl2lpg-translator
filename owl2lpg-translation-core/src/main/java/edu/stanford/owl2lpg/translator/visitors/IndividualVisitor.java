package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A visitor that contains the implementation to translate the OWL 2 individuals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IndividualVisitor extends VisitorBase
    implements OWLIndividualVisitorEx<Translation> {

  private Node mainNode;

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
    mainNode = createAnonymousIndividualNode(individual, NodeLabels.ANONYMOUS_INDIVIDUAL);
    return Translation.create(mainNode, ImmutableList.of(), ImmutableList.of());
  }

  @Override
  @Nullable
  protected Node getMainNode() {
    return mainNode;
  }

  @Nonnull
  @Override
  protected Translation getTranslation(OWLObject anyObject) {
    throw new IllegalArgumentException("Implementation error");
  }
}
