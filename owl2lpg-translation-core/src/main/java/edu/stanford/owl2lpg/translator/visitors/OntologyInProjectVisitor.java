package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.AnnotationObjectTranslator;
import edu.stanford.owl2lpg.translator.AnnotationValueTranslator;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.EntityInProjectTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyInProjectVisitor implements OWLNamedObjectVisitorEx<Translation> {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final ProjectContextNodeFactory projectContextNodeFactory;

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final EntityInProjectTranslator entityTranslator;

  @Nonnull
  private final AnnotationValueTranslator annotationValueTranslator;

  @Nonnull
  private final AnnotationObjectTranslator annotationObjectTranslator;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Inject
  public OntologyInProjectVisitor(@Nonnull ProjectId projectId,
                                  @Nonnull BranchId branchId,
                                  @Nonnull OntologyDocumentId ontoDocId,
                                  @Nonnull ProjectContextNodeFactory projectContextNodeFactory,
                                  @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                                  @Nonnull EntityInProjectTranslator entityTranslator,
                                  @Nonnull AnnotationValueTranslator annotationValueTranslator,
                                  @Nonnull AnnotationObjectTranslator annotationObjectTranslator,
                                  @Nonnull AxiomTranslator axiomTranslator) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.projectContextNodeFactory = checkNotNull(projectContextNodeFactory);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
    this.annotationObjectTranslator = checkNotNull(annotationObjectTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
  }

  @Nonnull
  @Override
  public Translation visit(OWLOntology ontology) {
    var projectNode = projectContextNodeFactory.createProjectNode(projectId);
    var branchNode = projectContextNodeFactory.createBranchNode(branchId);
    var ontoDocNode = projectContextNodeFactory.createOntologyDocumentNode(ontoDocId);

    var translations = new Builder<Translation>();
    var edges = new Builder<Edge>();
    translateOntologyIri(ontology.getOntologyID().getOntologyIRI(), ontoDocNode, translations, edges);
    translateVersionIri(ontology.getOntologyID().getVersionIRI(), ontoDocNode, translations, edges);
    translateOntologyAnnotations(ontology.getAnnotations(), ontoDocNode, translations, edges);

    return Translation.create(projectId,
        projectNode,
        ImmutableList.of(structuralEdgeFactory.getBranchStructuralEdge(projectNode, branchNode)),
        ImmutableList.of(Translation.create(branchId,
            branchNode,
            ImmutableList.of(structuralEdgeFactory.getOntologyDocumentStructuralEdge(branchNode, ontoDocNode)),
            ImmutableList.of(Translation.create(ontoDocId,
                ontoDocNode,
                edges.build(),
                translations.build())))));
  }

  private void translateOntologyIri(Optional<IRI> ontologyIri, Node ontoDocNode, Builder<Translation> translations, Builder<Edge> edges) {
    if (ontologyIri.isPresent()) {
      var ontologyIriTranslation = annotationValueTranslator.translate(ontologyIri.get());
      var ontologyIriEdge = structuralEdgeFactory.getOntologyIriStructuralEdge(ontoDocNode, ontologyIriTranslation.getMainNode());
      translations.add(ontologyIriTranslation);
      edges.add(ontologyIriEdge);
    }
  }

  private void translateVersionIri(Optional<IRI> versionIri, Node ontoDocNode, Builder<Translation> translations, Builder<Edge> edges) {
    if (versionIri.isPresent()) {
      var versionIriTranslation = annotationValueTranslator.translate(versionIri.get());
      var versionIriEdge = structuralEdgeFactory.getVersionIriStructuralEdge(ontoDocNode, versionIriTranslation.getMainNode());
      translations.add(versionIriTranslation);
      edges.add(versionIriEdge);
    }
  }

  private void translateOntologyAnnotations(Set<OWLAnnotation> ontologyAnnotations, Node ontoDocNode, Builder<Translation> translations, Builder<Edge> edges) {
    var ontologyAnnotationTranslations = ontologyAnnotations
        .stream()
        .map(annotationObjectTranslator::translate)
        .collect(ImmutableList.toImmutableList());
    var ontologyAnnotationEdges = ontologyAnnotationTranslations
        .stream()
        .map(translation -> structuralEdgeFactory.getOntologyAnnotationStructuralEdge(ontoDocNode, translation.getMainNode()))
        .collect(ImmutableList.toImmutableList());
    translations.addAll(ontologyAnnotationTranslations);
    edges.addAll(ontologyAnnotationEdges);
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
