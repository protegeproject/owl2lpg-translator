package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.model.GraphFactory.*;
import static edu.stanford.owl2lpg.translator.Translation.MainNode;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;

/**
 * A visitor that contains the implementation to translate the OWL 2 entities.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityVisitor extends VisitorBase
    implements OWLEntityVisitorEx<Translation> {

  @Override
  public Translation visit(@Nonnull OWLClass c) {
    var entityNode = Node(NodeLabels.CLASS,
        Properties(PropertyNames.IRI, String.valueOf(c.getIRI())),
        withIdentifierFrom(c));
    var iriTranslation = createIriTranslation(c);
    return Translation.create(entityNode,
        ImmutableList.of(
            Edge(entityNode, MainNode(iriTranslation), EdgeLabels.ENTITY_IRI)),
        ImmutableList.of(
            iriTranslation
        ));
  }

  @Override
  public Translation visit(@Nonnull OWLDatatype dt) {
    var entityNode = Node(NodeLabels.DATATYPE,
        Properties(PropertyNames.IRI, String.valueOf(dt.getIRI())),
        withIdentifierFrom(dt));
    var iriTranslation = createIriTranslation(dt);
    return Translation.create(entityNode,
        ImmutableList.of(
            Edge(entityNode, MainNode(iriTranslation), EdgeLabels.ENTITY_IRI)),
        ImmutableList.of(
            iriTranslation
        ));
  }

  @Override
  public Translation visit(@Nonnull OWLObjectProperty op) {
    var entityNode = Node(NodeLabels.OBJECT_PROPERTY,
        Properties(PropertyNames.IRI, String.valueOf(op.getIRI())),
        withIdentifierFrom(op));
    var iriTranslation = createIriTranslation(op);
    return Translation.create(entityNode,
        ImmutableList.of(
            Edge(entityNode, MainNode(iriTranslation), EdgeLabels.ENTITY_IRI)),
        ImmutableList.of(
            iriTranslation
        ));
  }

  @Override
  public Translation visit(@Nonnull OWLDataProperty dp) {
    var entityNode = Node(NodeLabels.DATA_PROPERTY,
        Properties(PropertyNames.IRI, String.valueOf(dp.getIRI())),
        withIdentifierFrom(dp));
    var iriTranslation = createIriTranslation(dp);
    return Translation.create(entityNode,
        ImmutableList.of(
            Edge(entityNode, MainNode(iriTranslation), EdgeLabels.ENTITY_IRI)),
        ImmutableList.of(
            iriTranslation
        ));
  }

  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty ap) {
    var entityNode = Node(NodeLabels.ANNOTATION_PROPERTY,
        Properties(PropertyNames.IRI, String.valueOf(ap.getIRI())),
        withIdentifierFrom(ap));
    var iriTranslation = createIriTranslation(ap);
    return Translation.create(entityNode,
        ImmutableList.of(
            Edge(entityNode, MainNode(iriTranslation), EdgeLabels.ENTITY_IRI)),
        ImmutableList.of(
            iriTranslation
        ));
  }

  @Override
  public Translation visit(@Nonnull OWLNamedIndividual a) {
    var entityNode = Node(NodeLabels.NAMED_INDIVIDUAL,
        Properties(PropertyNames.IRI, String.valueOf(a.getIRI())),
        withIdentifierFrom(a));
    var iriTranslation = createIriTranslation(a);
    return Translation.create(entityNode,
        ImmutableList.of(
            Edge(entityNode, MainNode(iriTranslation), EdgeLabels.ENTITY_IRI)),
        ImmutableList.of(
            iriTranslation
        ));
  }
}
