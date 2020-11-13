package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeIdProvider;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANONYMOUS_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.NODE_ID;

/**
 * A visitor that contains the implementation to translate the OWL 2 individuals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IndividualVisitor implements OWLIndividualVisitorEx<Translation> {

  @Nonnull
  private final EntityTranslator translator;

  @Nonnull
  private final NodeIdProvider nodeIdProvider;

  @Inject
  public IndividualVisitor(@Nonnull EntityTranslator translator,
                           @Nonnull NodeIdProvider nodeIdProvider) {
    this.translator = checkNotNull(translator);
    this.nodeIdProvider = checkNotNull(nodeIdProvider);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual individual) {
    return translator.translate(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    var nodeId = nodeIdProvider.getId(individual);
    var mainNode = Node.create(nodeId,
        ANONYMOUS_INDIVIDUAL,
        Properties.of(NODE_ID, String.valueOf(individual.getID())));
    return Translation.create(individual, mainNode, ImmutableList.of(), ImmutableList.of());
  }
}
