package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.Edge;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.Translation.MainNode;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.LEXICAL_FORM;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.NODE_ID;

public class AnnotationValueVisitor implements OWLAnnotationValueVisitorEx {

  @Nonnull
  private final OWLDataVisitorEx<Translation> dataVisitor;

  @Inject
  public AnnotationValueVisitor(@Nonnull OWLDataVisitorEx<Translation> dataVisitor) {
    this.dataVisitor = checkNotNull(dataVisitor);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    var iriNode = Node(NodeLabels.IRI, Properties(PropertyNames.IRI, iri.toString()));
    return Translation.create(iriNode, ImmutableList.of(), ImmutableList.of());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    var anonymousNode = Node(NodeLabels.ANONYMOUS_INDIVIDUAL, Properties(NODE_ID, individual.getID()));
    return Translation.create(anonymousNode, ImmutableList.of(), ImmutableList.of());
  }

  @Nonnull
  @Override
  public Object visit(@Nonnull OWLLiteral lt) {
    var literalNode = Node(NodeLabels.LITERAL, Properties(LEXICAL_FORM, lt.getLiteral()));
    var datatypeTranslation = lt.getDatatype().accept(dataVisitor);
    if (lt.isRDFPlainLiteral()) {
      var languageTagNode = Node(NodeLabels.LANGUAGE_TAG, Properties(PropertyNames.LANGUAGE, lt.getLang()));
      return Translation.create(literalNode,
          ImmutableList.of(
              Edge(literalNode, MainNode(datatypeTranslation), EdgeLabels.DATATYPE),
              Edge(literalNode, languageTagNode, EdgeLabels.LANGUAGE_TAG)),
          ImmutableList.of(
              datatypeTranslation));
    } else {
      return Translation.create(literalNode,
          ImmutableList.of(
              Edge(literalNode, MainNode(datatypeTranslation), EdgeLabels.DATATYPE)),
          ImmutableList.of(
              datatypeTranslation));
    }
  }
}
