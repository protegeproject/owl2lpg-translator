package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.NODE_ID;

public class AnnotationSubjectVisitor implements OWLAnnotationSubjectVisitorEx<Translation> {

  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    var iriNode = Node(NodeLabels.IRI, Properties(PropertyNames.IRI, iri.toString()));
    return Translation.create(iriNode, ImmutableList.of(), ImmutableList.of());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    var anonymousNode = Node(NodeLabels.ANONYMOUS_INDIVIDUAL, Properties(NODE_ID, individual.getID().toString()));
    return Translation.create(anonymousNode, ImmutableList.of(), ImmutableList.of());
  }
}
