package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.EdgeFactory;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A visitor that contains the implementation to translate the OWL 2 individuals.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IndividualVisitor implements OWLIndividualVisitorEx<Translation> {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Nonnull
  private final EntityTranslator translator;

  @Inject
  public IndividualVisitor(@Nonnull NodeFactory nodeFactory,
                           @Nonnull EdgeFactory edgeFactory,
                           @Nonnull EntityTranslator translator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
    this.translator = checkNotNull(translator);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual individual) {
    return translator.translate(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    var mainNode = nodeFactory.createNode(individual, NodeLabels.ANONYMOUS_INDIVIDUAL,
        Properties.of(PropertyFields.NODE_ID, String.valueOf(individual.getID())));
    return Translation.create(mainNode, ImmutableList.of(), ImmutableList.of());
  }
}
