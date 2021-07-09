package edu.stanford.owl2lpg.exporter.graphml;

import edu.stanford.owl2lpg.exporter.common.internal.ProjectTranslator;
import edu.stanford.owl2lpg.exporter.graphml.writer.Neo4jGraphmlWriter;
import edu.stanford.owl2lpg.model.AugmentedEdgeFactory;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.AnnotationObjectTranslator;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_DOCUMENT;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyGraphmlExporter {

  @Nonnull
  private final ProjectTranslator projectTranslator;

  @Nonnull
  private final AnnotationObjectTranslator annotationTranslator;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final AugmentedEdgeFactory augmentedEdgeFactory;

  @Nonnull
  private final Neo4jGraphmlWriter graphmlWriter;

  @Inject
  public OntologyGraphmlExporter(@Nonnull ProjectTranslator projectTranslator,
                                 @Nonnull AnnotationObjectTranslator annotationTranslator,
                                 @Nonnull AxiomTranslator axiomTranslator,
                                 @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                                 @Nonnull AugmentedEdgeFactory augmentedEdgeFactory,
                                 @Nonnull Neo4jGraphmlWriter graphmlWriter) {
    this.projectTranslator = checkNotNull(projectTranslator);
    this.annotationTranslator = checkNotNull(annotationTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.augmentedEdgeFactory = checkNotNull(augmentedEdgeFactory);
    this.graphmlWriter = checkNotNull(graphmlWriter);
  }

  public void export(@Nonnull OWLOntology ontology) throws IOException {
    export(ontology, ProjectId.create(), BranchId.create(), OntologyDocumentId.create());
  }

  public void export(@Nonnull OWLOntology ontology,
                     @Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId documentId) throws IOException {
    var projectTranslation = projectTranslator
            .translate(ontology.getOntologyID(), projectId, branchId, documentId);
    writeTranslation(projectTranslation);

    var documentNode = projectTranslation.nodes(ONTOLOGY_DOCUMENT).findFirst().get();
    writeOntologyAnnotations(ontology.getAnnotations(), documentNode);
    writeOntologyAxioms(ontology.getAxioms(), documentNode);

    graphmlWriter.printReport();

    graphmlWriter.close();
  }

  private void writeTranslation(Translation translation) {
    graphmlWriter.writeTranslation(translation);
  }

  private void writeOntologyAnnotations(Set<OWLAnnotation> annotations, Node documentNode) {
    annotations.forEach(annotation -> {
      var annotationTranslation = annotationTranslator.translate(annotation);
      writeTranslation(annotationTranslation);
      writeOntologyAnnotationEdge(annotationTranslation, documentNode);
    });
  }

  private void writeOntologyAnnotationEdge(Translation annotationTranslation, Node documentNode) {
    var annotationNode = annotationTranslation.getMainNode();
    var ontologyAnnotationEdge = structuralEdgeFactory.getOntologyAnnotationEdge(documentNode, annotationNode);
    graphmlWriter.writeEdge(ontologyAnnotationEdge);
  }

  private void writeOntologyAxioms(Set<OWLAxiom> axioms, Node documentNode) {
    axioms.stream()
        .map(axiomTranslator::translate)
        .forEach(axiomTranslation -> {
          writeTranslation(axiomTranslation);
          writeAxiomEdge(axiomTranslation, documentNode);
          writeInOntologySignatureEdge(axiomTranslation, documentNode);
        });
  }

  private void writeAxiomEdge(Translation axiomTranslation, Node documentNode) {
    var axiomNode = axiomTranslation.getMainNode();
    var axiomEdge = structuralEdgeFactory.getAxiomEdge(documentNode, axiomNode);
    graphmlWriter.writeEdge(axiomEdge);
  }

  private void writeInOntologySignatureEdge(Translation axiomTranslation, Node documentNode) {
    var entityNodes = axiomTranslation.nodes(ENTITY);
    entityNodes.forEach(entityNode ->
        augmentedEdgeFactory.getInOntologySignatureEdge(entityNode, documentNode)
            .ifPresent(graphmlWriter::writeEdge));
  }
}
