package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.exporter.csv.internal.ProjectTranslator;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvOutputWriter;
import edu.stanford.owl2lpg.model.AugmentedEdgeFactory;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.AnnotationObjectTranslator;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_DOCUMENT;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyCsvExporter {

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
  private final CsvOutputWriter csvOutputWriter;

  @Inject
  public OntologyCsvExporter(@Nonnull ProjectTranslator projectTranslator,
                             @Nonnull AnnotationObjectTranslator annotationTranslator,
                             @Nonnull AxiomTranslator axiomTranslator,
                             @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                             @Nonnull AugmentedEdgeFactory augmentedEdgeFactory,
                             @Nonnull CsvOutputWriter csvOutputWriter) {
    this.projectTranslator = checkNotNull(projectTranslator);
    this.annotationTranslator = checkNotNull(annotationTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.augmentedEdgeFactory = checkNotNull(augmentedEdgeFactory);
    this.csvOutputWriter = checkNotNull(csvOutputWriter);
  }

  public void export(@Nonnull OWLOntology ontology) {
    export(ontology, ProjectId.generate(), BranchId.generate(), OntologyDocumentId.generate());
  }

  public void export(@Nonnull OWLOntology ontology,
                     @Nonnull UUID projectUuid,
                     @Nonnull UUID branchUuid,
                     @Nonnull UUID ontDocUuid) {
    export(ontology,
        ProjectId.get(projectUuid.toString()),
        BranchId.get(branchUuid.toString()),
        OntologyDocumentId.get(ontDocUuid.toString()));
  }

  public void export(@Nonnull OWLOntology ontology,
                     @Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId ontDocId) {
    export(ontology.getOntologyID(), ontology.getAnnotations(), ontology.getAxioms(),
        ontology.getImportsDeclarations(),
        projectId, branchId, ontDocId);
  }

  public void export(@Nonnull OWLOntologyID ontologyId,
                     @Nonnull Set<OWLAnnotation> ontologyAnnotations,
                     @Nonnull Set<OWLAxiom> axioms,
                     @Nonnull Set<OWLImportsDeclaration> importsDeclarations,
                     @Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId ontDocId) {
    var projectTranslation = projectTranslator.translate(ontologyId, projectId, branchId, ontDocId);
    writeTranslation(projectTranslation);
    projectTranslation.nodes(ONTOLOGY_DOCUMENT).findFirst()
        .ifPresent(documentNode -> {
          writeOntologyAnnotations(ontologyAnnotations, documentNode);
          writeOntologyAxioms(axioms, documentNode);
        });
    csvOutputWriter.printReport();
  }

  private void writeTranslation(Translation translation) {
    csvOutputWriter.write(translation);
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
    csvOutputWriter.write(ontologyAnnotationEdge);
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
    csvOutputWriter.write(axiomEdge);
  }

  private void writeInOntologySignatureEdge(Translation axiomTranslation, Node documentNode) {
    var entityNodes = axiomTranslation.nodes(ENTITY);
    entityNodes.forEach(entityNode ->
        augmentedEdgeFactory.getInOntologySignatureEdge(entityNode, documentNode)
            .ifPresent(csvOutputWriter::write));
  }
}
