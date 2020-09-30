package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.IndividualTranslator;
import edu.stanford.owl2lpg.translator.LiteralTranslator;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A visitor that contains the implementation to translate the OWL 2 annotation values.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationValueVisitor implements OWLAnnotationValueVisitorEx<Translation> {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final IndividualTranslator individualTranslator;

  @Nonnull
  private final LiteralTranslator literalTranslator;

  @Inject
  public AnnotationValueVisitor(@Nonnull NodeFactory nodeFactory,
                                @Nonnull IndividualTranslator individualTranslator,
                                @Nonnull LiteralTranslator literalTranslator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.individualTranslator = checkNotNull(individualTranslator);
    this.literalTranslator = checkNotNull(literalTranslator);
  }


  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    var mainNode = nodeFactory.createNode(iri, NodeLabels.IRI,
        Properties.of(PropertyFields.IRI, String.valueOf(iri)));
    return Translation.create(iri, mainNode);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    return individualTranslator.translate(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLLiteral lt) {
    return literalTranslator.translate(lt);
  }
}
