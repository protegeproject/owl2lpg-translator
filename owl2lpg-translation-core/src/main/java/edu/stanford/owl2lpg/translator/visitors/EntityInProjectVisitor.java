package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.AnnotationValueTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.jetbrains.annotations.NotNull;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.net.URLDecoder;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATATYPE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.NAMED_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@TranslationSessionScope
public class EntityInProjectVisitor implements OWLEntityVisitorEx<Translation> {

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final ProjectContextNodeFactory projectContextNodeFactory;

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final AugmentedEdgeFactory augmentedEdgeFactory;

  @Nonnull
  private final AnnotationValueTranslator annotationValueTranslator;

  @Inject
  public EntityInProjectVisitor(@Nonnull OntologyDocumentId ontoDocId,
                                @Nonnull NodeFactory nodeFactory,
                                @Nonnull ProjectContextNodeFactory projectContextNodeFactory,
                                @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                                @Nonnull AugmentedEdgeFactory augmentedEdgeFactory,
                                @Nonnull AnnotationValueTranslator annotationValueTranslator) {
    this.ontoDocId = checkNotNull(ontoDocId);
    this.nodeFactory = checkNotNull(nodeFactory);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.projectContextNodeFactory = checkNotNull(projectContextNodeFactory);
    this.augmentedEdgeFactory = checkNotNull(augmentedEdgeFactory);
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
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
    var entityNode = createEntityNode(entity, nodeLabels);
    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    translateEntityIri(entity.getIRI(), entityNode, translations, edges);
    translateEntitySignatureOf(ontoDocId, entityNode, translations, edges);
    return Translation.create(entity,
        entityNode,
        edges.build(),
        translations.build());
  }

  @NotNull
  private Node createEntityNode(OWLEntity entity, NodeLabels nodeLabels) {
    return nodeFactory.createNode(entity, nodeLabels,
        Properties.create(ImmutableMap.of(
            PropertyFields.IRI, getIriString(entity.getIRI()),
            PropertyFields.IRI_SUFFIX, getLocalName(entity.getIRI()))));
  }

  private void translateEntityIri(IRI entityIri, Node entityNode,
                                  Builder<Translation> translations, Builder<Edge> edges) {
    var iriTranslation = annotationValueTranslator.translate(entityIri);
    var entityIriEdge = structuralEdgeFactory.getEntityIriStructuralEdge(entityNode, iriTranslation.getMainNode());
    translations.add(iriTranslation);
    edges.add(entityIriEdge);
  }

  private void translateEntitySignatureOf(OntologyDocumentId ontoDocId, Node entityNode,
                                          Builder<Translation> translations, Builder<Edge> edges) {
    var ontologyDocumentTranslation = Translation.create(ontoDocId,
        projectContextNodeFactory.createOntologyDocumentNode(ontoDocId),
        ImmutableList.of(),
        ImmutableList.of());
    var entitySignatureOfEdge = augmentedEdgeFactory.getEntitySignatureOfAugmentedEdge(entityNode, ontologyDocumentTranslation.getMainNode());
    translations.add(ontologyDocumentTranslation);
    entitySignatureOfEdge.ifPresent(edges::add);
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

  private static String getIriString(IRI iri) {
    return iri.toString();
  }

  private static String decode(@Nonnull String localName) {
    return URLDecoder.decode(localName, Charsets.UTF_8);
  }
}
