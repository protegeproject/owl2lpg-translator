package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.IndividualTranslator;
import edu.stanford.owl2lpg.translator.LiteralTranslator;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectDigester;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.jetbrains.annotations.NotNull;
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
  private final IndividualTranslator individualTranslator;

  @Nonnull
  private final LiteralTranslator literalTranslator;

  @Nonnull
  private final OntologyObjectDigester ontologyObjectDigester;

  @Inject
  public AnnotationValueVisitor(@Nonnull IndividualTranslator individualTranslator,
                                @Nonnull LiteralTranslator literalTranslator,
                                @Nonnull OntologyObjectDigester ontologyObjectDigester) {
    this.individualTranslator = checkNotNull(individualTranslator);
    this.literalTranslator = checkNotNull(literalTranslator);
    this.ontologyObjectDigester = checkNotNull(ontologyObjectDigester);
  }


  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    var mainNode = createIriNode(iri);
    return Translation.create(iri, mainNode);
  }

  @NotNull
  private Node createIriNode(@Nonnull IRI iri) {
    var digestString = ontologyObjectDigester.getDigest(iri);
    var nodeId = NodeId.create(digestString);
    return Node.create(nodeId, NodeLabels.IRI,
        Properties.of(PropertyFields.IRI, String.valueOf(iri)));
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
