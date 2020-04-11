package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;

public class AnnotationSubjectVisitor extends VisitorBase
    implements OWLAnnotationSubjectVisitorEx<Translation> {

  private Node mainNode;

  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    mainNode = createIriNode(iri, NodeLabels.IRI);
    return Translation.create(mainNode, ImmutableList.of(), ImmutableList.of());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    mainNode = createAnonymousIndividualNode(individual, NodeLabels.ANONYMOUS_INDIVIDUAL);
    return Translation.create(mainNode, ImmutableList.of(), ImmutableList.of());
  }

  @Nonnull
  @Override
  protected Node getMainNode() {
    return mainNode;
  }

  @Override
  protected Translation getTranslation(@Nonnull OWLObject anyObject) {
    throw new IllegalArgumentException("Implementation error");
  }
}
