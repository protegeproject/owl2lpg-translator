package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.AugmentedEdgeFactory;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.OntologyContextNodeFactory;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.translator.AnnotationValueTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.shared.BuiltInPrefixDeclarations;
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
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATATYPE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.NAMED_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY;

/**
 * A visitor that contains the implementation to translate the OWL 2 entities.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@TranslationSessionScope
public class EntityVisitor implements OWLEntityVisitorEx<Translation> {

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final OntologyContextNodeFactory ontologyContextNodeFactory;

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final AugmentedEdgeFactory augmentedEdgeFactory;

  @Nonnull
  private final AnnotationValueTranslator annotationValueTranslator;

  @Nonnull
  private final BuiltInPrefixDeclarations builtInPrefixDeclarations;

  @Inject
  public EntityVisitor(@Nonnull OntologyDocumentId ontoDocId,
                       @Nonnull NodeFactory nodeFactory,
                       @Nonnull OntologyContextNodeFactory ontologyContextNodeFactory,
                       @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                       @Nonnull AugmentedEdgeFactory augmentedEdgeFactory,
                       @Nonnull AnnotationValueTranslator annotationValueTranslator,
                       @Nonnull BuiltInPrefixDeclarations builtInPrefixDeclarations) {
    this.ontoDocId = checkNotNull(ontoDocId);
    this.nodeFactory = checkNotNull(nodeFactory);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.ontologyContextNodeFactory = checkNotNull(ontologyContextNodeFactory);
    this.augmentedEdgeFactory = checkNotNull(augmentedEdgeFactory);
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
    this.builtInPrefixDeclarations = checkNotNull(builtInPrefixDeclarations);
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
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();
    translateEntityIri(entity.getIRI(), entityNode, translations, edges);
    translateEntitySignatureOf(ontoDocId, entityNode, translations, edges);
    return Translation.create(entity,
        entityNode,
        edges.build(),
        translations.build());
  }

  @NotNull
  private Node createEntityNode(OWLEntity entity, NodeLabels nodeLabels) {
    IRI entityIRI = entity.getIRI();
    return nodeFactory.createNode(entity, nodeLabels,
        Properties.create(ImmutableMap.of(
            PropertyFields.IRI, getIriString(entityIRI),
            PropertyFields.LOCAL_NAME, getLocalName(entityIRI),
            PropertyFields.PREFIXED_NAME, getPrefixedName(entityIRI),
            PropertyFields.OBO_ID, getOboId(entityIRI))));
  }

  private void translateEntityIri(IRI entityIri, Node entityNode,
                                  ImmutableList.Builder<Translation> translations, ImmutableList.Builder<Edge> edges) {
    var iriTranslation = annotationValueTranslator.translate(entityIri);
    var entityIriEdge = structuralEdgeFactory.getEntityIriEdge(entityNode, iriTranslation.getMainNode());
    translations.add(iriTranslation);
    edges.add(entityIriEdge);
  }

  private void translateEntitySignatureOf(OntologyDocumentId ontoDocId, Node entityNode,
                                          ImmutableList.Builder<Translation> translations, ImmutableList.Builder<Edge> edges) {
    var ontologyDocumentTranslation = Translation.create(ontoDocId,
        ontologyContextNodeFactory.createOntologyDocumentNode(ontoDocId),
        ImmutableList.of(),
        ImmutableList.of());
    var entitySignatureOfEdge = augmentedEdgeFactory.getEntitySignatureOfEdge(entityNode, ontologyDocumentTranslation.getMainNode());
    translations.add(ontologyDocumentTranslation);
    entitySignatureOfEdge.ifPresent(edges::add);
  }

  @Nonnull
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

  @Nonnull
  private static String getOboId(@Nonnull IRI iri) {
    var oboIdPattern = Pattern.compile("/([A-Z|a-z]+(_[A-Z|a-z]+)?)_([0-9]+)$");
    var iriString = getIriString(iri);
    var matcher = oboIdPattern.matcher(iriString);
    return (matcher.find()) ? matcher.group(1) + ":" + matcher.group(3) : "";
  }

  @Nonnull
  private String getPrefixedName(@Nonnull IRI iri) {
    var iriPrefix = iri.getNamespace();
    var prefixName = builtInPrefixDeclarations.getPrefixName(iriPrefix);
    return (prefixName != null) ? prefixName + getLocalName(iri) : "";
  }

  private static String getIriString(IRI iri) {
    return iri.toString();
  }

  private static String decode(@Nonnull String localName) {
    return URLDecoder.decode(localName, Charsets.UTF_8);
  }
}
