package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.utils.PropertiesBuilder;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.*;

/**
 * A visitor that contains the implementation to translate the OWL 2 ontology.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyVisitor extends VisitorBase
    implements OWLNamedObjectVisitorEx<Translation> {

  private Node mainNode;

  @Nonnull
  private final OWLAxiomVisitorEx<Translation> axiomVisitor;

  @Nonnull
  private final OWLAnnotationObjectVisitorEx<Translation> annotationVisitor;

  @Inject
  public OntologyVisitor(@Nonnull OWLAxiomVisitorEx<Translation> axiomVisitor,
                         @Nonnull OWLAnnotationObjectVisitorEx<Translation> annotationVisitor) {
    this.axiomVisitor = checkNotNull(axiomVisitor);
    this.annotationVisitor = checkNotNull(annotationVisitor);
  }

  @Nonnull
  public Translation visit(@Nonnull OWLOntology ontology) {
    mainNode = createNode(ontology, NodeLabels.ONTOLOGY_DOCUMENT);
    var ontologyIdEdge = createOntologyIdEdge(ontology.getOntologyID());
    var ontologyIdTranslation = createOntologyIdTranslation(ontology.getOntologyID());
    var ontologyAnnotationEdges = createEdges(ontology.getAnnotations(), EdgeLabels.ONTOLOGY_ANNOTATION);
    var ontologyAnnotationTranslations = createNestedTranslations(ontology.getAnnotations());
    var ontologyAxiomEdges = createEdges(ontology.getAxioms(), EdgeLabels.AXIOM);
    var ontologyAxiomTranslations = createNestedTranslations(ontology.getAxioms());
    var allEdges = Stream.concat(Lists.newArrayList(ontologyIdEdge).stream(),
        Stream.concat(ontologyAnnotationEdges.stream(), ontologyAxiomEdges.stream()))
        .collect(Collectors.toList());
    var allTranslations = Stream.concat(Lists.newArrayList(ontologyIdTranslation).stream(),
        Stream.concat(ontologyAnnotationTranslations.stream(), ontologyAxiomTranslations.stream()))
        .collect(Collectors.toList());
    return Translation.create(mainNode, ImmutableList.copyOf(allEdges), ImmutableList.copyOf(allTranslations));
  }

  protected Edge createOntologyIdEdge(OWLOntologyID ontologyId) {
    return Edge(mainNode, createOntologyIdNode(ontologyId), EdgeLabels.ONTOLOGY_ID);
  }

  private Node createOntologyIdNode(OWLOntologyID ontologyId) {
    String ontologyIri = ontologyId.getOntologyIRI().isPresent() ? ontologyId.getOntologyIRI().toString() : null;
    String versionIri = ontologyId.getVersionIRI().isPresent() ? ontologyId.getVersionIRI().toString() : null;
    return Node(
        NodeLabels.ONTOLOGY_ID,
        PropertiesBuilder.create()
            .set(PropertyNames.ONTOLOGY_IRI, ontologyIri)
            .set(PropertyNames.ONTOLOGY_VERSION_IRI, versionIri)
            .build(),
        withIdentifierFrom(ontologyId));
  }

  protected Translation createOntologyIdTranslation(OWLOntologyID ontologyID) {
    return Translation.create(createOntologyIdNode(ontologyID), ImmutableList.of(), ImmutableList.of());
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLClass owlClass) {
    throw new UnsupportedOperationException("Use the EntityVisitor to visit OWLClass");
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectProperty property) {
    throw new UnsupportedOperationException("Use the EntityVisitor to visit OWLObjectProperty");
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataProperty property) {
    throw new UnsupportedOperationException("Use the EntityVisitor to visit OWLDataProperty");
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLNamedIndividual owlIndividual) {
    throw new UnsupportedOperationException("Use the EntityVisitor to visit OWLNamedIndividual");
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDatatype datatype) {
    throw new UnsupportedOperationException("Use the EntityVisitor to visit OWLDatatype");
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty property) {
    throw new UnsupportedOperationException("Use the EntityVisitor to visit OWLAnnotationProperty");
  }

  @Override
  protected Node getMainNode() {
    return mainNode;
  }

  @Override
  protected Translation getTranslation(@Nonnull OWLObject anyObject) {
    checkNotNull(anyObject);
    if (anyObject instanceof OWLAnnotation) {
      return ((OWLAnnotation) anyObject).accept(annotationVisitor);
    } else if (anyObject instanceof OWLAxiom) {
      return ((OWLAxiom) anyObject).accept(axiomVisitor);
    }
    throw new IllegalArgumentException("Implementation error");
  }
}
