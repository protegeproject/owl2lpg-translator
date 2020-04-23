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
import static edu.stanford.owl2lpg.model.GraphFactory.Edge;
import static edu.stanford.owl2lpg.model.GraphFactory.Node;

/**
 * A visitor that contains the implementation to translate the OWL 2 ontology.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyVisitor extends VisitorBase
    implements OWLNamedObjectVisitorEx<Translation> {

  private Node mainNode;

  private final VisitorFactory visitorFactory;

  @Inject
  public OntologyVisitor(@Nonnull NodeIdMapper nodeIdMapper,
                         @Nonnull VisitorFactory visitorFactory) {
    super(nodeIdMapper);
    this.visitorFactory = checkNotNull(visitorFactory);
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

  protected Translation createOntologyIdTranslation(OWLOntologyID ontologyID) {
    return Translation.create(
        createOntologyIdNode(ontologyID),
        ImmutableList.of(),
        ImmutableList.of());
  }

  private Node createOntologyIdNode(@Nonnull OWLOntologyID ontologyId) {
    var ontologyIri = ontologyId.getOntologyIRI().isPresent() ? ontologyId.getOntologyIRI().toString() : null;
    var versionIri = ontologyId.getVersionIRI().isPresent() ? ontologyId.getVersionIRI().toString() : null;
    return Node(
        nodeIdMapper.get(ontologyId),
        NodeLabels.ONTOLOGY_ID,
        PropertiesBuilder.create()
            .set(PropertyNames.ONTOLOGY_IRI, ontologyIri)
            .set(PropertyNames.ONTOLOGY_VERSION_IRI, versionIri)
            .build());
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
      return getAnnotationTranslation((OWLAnnotation) anyObject);
    } else if (anyObject instanceof OWLAxiom) {
      return getAxiomTranslation((OWLAxiom) anyObject);
    }
    throw new IllegalArgumentException("Implementation error");
  }

  private Translation getAnnotationTranslation(OWLAnnotation annotation) {
    var annotationVisitor = visitorFactory.createAnnotationObjectVisitor();
    return annotation.accept(annotationVisitor);
  }

  private Translation getAxiomTranslation(OWLAxiom axiom) {
    var axiomVisitor = visitorFactory.createAxiomVisitor();
    return axiom.accept(axiomVisitor);
  }
}
