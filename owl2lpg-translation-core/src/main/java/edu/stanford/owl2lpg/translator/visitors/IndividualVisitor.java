package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectDigester;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANONYMOUS_INDIVIDUAL;

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
  private final OntologyObjectDigester ontologyObjectDigester;

  @Inject
  public IndividualVisitor(@Nonnull EntityTranslator translator,
                           @Nonnull OntologyObjectDigester ontologyObjectDigester) {
    this.translator = checkNotNull(translator);
    this.ontologyObjectDigester = checkNotNull(ontologyObjectDigester);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual individual) {
    return translator.translate(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    var digestString = ontologyObjectDigester.getDigest(individual);
    var nodeId = NodeId.create(digestString);
    var mainNode = Node.create(nodeId,
        ANONYMOUS_INDIVIDUAL,
        Properties.of(
            PropertyFields.NODE_ID, String.valueOf(individual.getID()),
            PropertyFields.DIGEST, digestString));
    return Translation.create(individual, mainNode, ImmutableList.of(), ImmutableList.of());
  }
}
