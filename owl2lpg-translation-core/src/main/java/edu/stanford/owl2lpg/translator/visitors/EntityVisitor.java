package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.EdgeFactory;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.AnnotationValueTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.net.URLDecoder;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.*;

/**
 * A visitor that contains the implementation to translate the OWL 2 entities.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@TranslationSessionScope
public class EntityVisitor implements OWLEntityVisitorEx<Translation> {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Nonnull
  private final AnnotationValueTranslator translator;

  @Inject
  public EntityVisitor(@Nonnull NodeFactory nodeFactory,
                       @Nonnull EdgeFactory edgeFactory,
                       @Nonnull AnnotationValueTranslator translator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
    this.translator = checkNotNull(translator);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClass c) {
    return translateEntity(c, CLASS);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatype dt) {
    return translateEntity(dt, DATATYPE);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectProperty op) {
    return translateEntity(op, OBJECT_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataProperty dp) {
    return translateEntity(dp, DATA_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty ap) {
    return translateEntity(ap, ANNOTATION_PROPERTY);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual ind) {
    return translateEntity(ind, NAMED_INDIVIDUAL);
  }

  private Translation translateEntity(OWLEntity entity, NodeLabels nodeLabels) {
    var mainNode = nodeFactory.createNode(entity, nodeLabels,
        Properties.create(ImmutableMap.of(
            PropertyFields.IRI, getIriString(entity.getIRI()),
            PropertyFields.IRI_SUFFIX, getLocalName(entity.getIRI()))));
    var iriTranslation = translator.translate(entity.getIRI());
    var entityIriEdge = edgeFactory.createEdge(mainNode,
        iriTranslation.getMainNode(),
        EdgeLabel.ENTITY_IRI);
    return Translation.create(entity,
        mainNode,
        ImmutableList.of(entityIriEdge),
        ImmutableList.of(iriTranslation));
  }

  private static String getIriString(IRI iri) {
    return iri.toString();
  }

  private static String getLocalName(@Nonnull IRI iri) {
    String iriString = getIriString(iri);
    int hashIndex = iriString.lastIndexOf("#");
    if (hashIndex != -1 && hashIndex < iriString.length() - 1) {
      return decode(iriString.substring(hashIndex + 1));
    }
    int slashIndex = iriString.lastIndexOf("/");
    if (slashIndex != -1 && slashIndex < iriString.length() - 1) {
      return decode(iriString.substring(slashIndex + 1));
    }
    return "";
  }

  private static String decode(@Nonnull String localName) {
    return URLDecoder.decode(localName, Charsets.UTF_8);
  }
}
