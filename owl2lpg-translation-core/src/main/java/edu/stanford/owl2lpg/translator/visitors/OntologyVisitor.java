package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.AnnotationObjectTranslator;
import edu.stanford.owl2lpg.translator.AnnotationValueTranslator;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_DOCUMENT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;

/**
 * A visitor that contains the implementation to translate the OWL 2 ontology.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyVisitor implements OWLNamedObjectVisitorEx<Translation> {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final EntityTranslator entityTranslator;

  @Nonnull
  private final AnnotationValueTranslator annotationValueTranslator;

  @Nonnull
  private final AnnotationObjectTranslator annotationObjectTranslator;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Inject
  public OntologyVisitor(@Nonnull NodeFactory nodeFactory,
                         @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                         @Nonnull EntityTranslator entityTranslator,
                         @Nonnull AnnotationValueTranslator annotationValueTranslator,
                         @Nonnull AnnotationObjectTranslator annotationObjectTranslator,
                         @Nonnull AxiomTranslator axiomTranslator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
    this.annotationObjectTranslator = checkNotNull(annotationObjectTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
  }

  @Nonnull
  @Override
  public Translation visit(OWLOntology ontology) {
    var translations = new ImmutableList.Builder<Translation>();
    var edges = new ImmutableList.Builder<Edge>();

    var documentId = UUID.randomUUID();
    var documentNode = createDocumentNode(documentId);
    translateOntologyIri(ontology.getOntologyID().getOntologyIRI(), documentNode, translations, edges);
    translateVersionIri(ontology.getOntologyID().getVersionIRI(), documentNode, translations, edges);
    translateOntologyAnnotations(ontology.getAnnotations(), documentNode, translations, edges);
    translateOntologyAxioms(ontology.getAxioms(), documentNode, translations, edges);

    return Translation.create(documentId, documentNode, edges.build(), translations.build());
  }

  @Nonnull
  private Node createDocumentNode(UUID documentId) {
    return nodeFactory.createNode(documentId, ONTOLOGY_DOCUMENT, Properties.of(ONTOLOGY_DOCUMENT_ID, String.valueOf(documentId)));
  }

  private void translateOntologyIri(Optional<IRI> ontologyIri, Node documentNode,
                                    ImmutableList.Builder<Translation> translations,
                                    ImmutableList.Builder<Edge> edges) {
    if (ontologyIri.isPresent()) {
      var ontologyIriTranslation = annotationValueTranslator.translate(ontologyIri.get());
      var ontologyIriEdge = structuralEdgeFactory.getOntologyIriEdge(documentNode, ontologyIriTranslation.getMainNode());
      translations.add(ontologyIriTranslation);
      edges.add(ontologyIriEdge);
    }
  }

  private void translateVersionIri(Optional<IRI> versionIri, Node documentNode,
                                   ImmutableList.Builder<Translation> translations,
                                   ImmutableList.Builder<Edge> edges) {
    if (versionIri.isPresent()) {
      var versionIriTranslation = annotationValueTranslator.translate(versionIri.get());
      var versionIriEdge = structuralEdgeFactory.getVersionIriEdge(documentNode, versionIriTranslation.getMainNode());
      translations.add(versionIriTranslation);
      edges.add(versionIriEdge);
    }
  }

  private void translateOntologyAnnotations(Set<OWLAnnotation> ontologyAnnotations, Node documentNode,
                                            ImmutableList.Builder<Translation> translations,
                                            ImmutableList.Builder<Edge> edges) {
    var ontologyAnnotationTranslations = ontologyAnnotations
        .stream()
        .map(annotationObjectTranslator::translate)
        .collect(ImmutableList.toImmutableList());
    var ontologyAnnotationEdges = ontologyAnnotationTranslations
        .stream()
        .map(translation -> structuralEdgeFactory.getOntologyAnnotationEdge(documentNode, translation.getMainNode()))
        .collect(ImmutableList.toImmutableList());
    translations.addAll(ontologyAnnotationTranslations);
    edges.addAll(ontologyAnnotationEdges);
  }

  private void translateOntologyAxioms(Set<OWLAxiom> ontologyAxioms, Node documentNode,
                                       ImmutableList.Builder<Translation> translations,
                                       ImmutableList.Builder<Edge> edges) {
    var ontologyAxiomTranslations = ontologyAxioms
        .stream()
        .map(axiomTranslator::translate)
        .collect(ImmutableList.toImmutableList());
    var ontologyAxiomEdges = ontologyAxiomTranslations
        .stream()
        .map(translation -> structuralEdgeFactory.getAxiomEdge(documentNode, translation.getMainNode()))
        .collect(ImmutableList.toImmutableList());
    translations.addAll(ontologyAxiomTranslations);
    edges.addAll(ontologyAxiomEdges);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClass clasz) {
    return entityTranslator.translate(clasz);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectProperty property) {
    return entityTranslator.translate(property);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataProperty property) {
    return entityTranslator.translate(property);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual individual) {
    return entityTranslator.translate(individual);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatype datatype) {
    return entityTranslator.translate(datatype);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty property) {
    return entityTranslator.translate(property);
  }
}
