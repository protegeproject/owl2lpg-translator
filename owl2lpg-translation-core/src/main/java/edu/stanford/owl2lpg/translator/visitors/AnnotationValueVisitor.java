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

public class AnnotationValueVisitor extends VisitorBase
    implements OWLAnnotationValueVisitorEx<Translation> {

  @Nonnull
  private final OWLDataVisitorEx<Translation> dataVisitor;

  private Node mainNode;

  @Inject
  public AnnotationValueVisitor(@Nonnull OWLDataVisitorEx<Translation> dataVisitor) {
    this.dataVisitor = checkNotNull(dataVisitor);
  }

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
  public Translation visit(@Nonnull OWLLiteral lt) {
    checkNotNull(lt);
    return dataVisitor.visit(lt);
  }

  @Nullable
  @Override
  protected Node getMainNode() {
    return mainNode;
  }

  @Override
  protected Translation getTranslation(@Nonnull OWLObject anyObject) {
    throw new IllegalArgumentException("Implementation error");
  }
}
