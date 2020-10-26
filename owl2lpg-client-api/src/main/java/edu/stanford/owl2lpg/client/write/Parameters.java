package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Parameters {

  private static final String PROJECT_ID = "projectId";
  private static final String BRANCH_ID = "branchId";
  private static final String ONTO_DOC_ID = "ontoDocId";
  private static final String ANNOTATION_SUBJECT_IRI = "annotationSubjectIri";
  private static final String ANNOTATION_PROPERTY_IRI = "annotationPropertyIri";
  private static final String LEXICAL_FORM = "lexicalForm";
  private static final String DATATYPE = "datatype";
  private static final String LANGUAGE = "language";
  private static final String IRI = "iri";
  private static final String DIGEST = "digest";

  @Nonnull
  public static Value forAnnotationAssertionAxiom(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId,
                                                  @Nonnull String axiomDigest,
                                                  @Nonnull IRI annotationSubjectIri,
                                                  @Nonnull IRI annotationPropertyIri,
                                                  @Nonnull OWLLiteral literalValue) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        DIGEST, new StringValue(axiomDigest),
        ANNOTATION_SUBJECT_IRI, new StringValue(annotationSubjectIri.toString()),
        ANNOTATION_PROPERTY_IRI, new StringValue(annotationPropertyIri.toString()),
        LEXICAL_FORM, new StringValue(literalValue.getLiteral()),
        DATATYPE, new StringValue(literalValue.getDatatype().getIRI().toString())));
  }

  @Nonnull
  public static Value forAnnotationAssertionAxiomWithLanguageTag(@Nonnull ProjectId projectId,
                                                                 @Nonnull BranchId branchId,
                                                                 @Nonnull OntologyDocumentId ontoDocId,
                                                                 @Nonnull String axiomDigest,
                                                                 @Nonnull IRI annotationSubjectIri,
                                                                 @Nonnull IRI annotationPropertyIri,
                                                                 @Nonnull OWLLiteral literalValue) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        DIGEST, new StringValue(axiomDigest),
        ANNOTATION_SUBJECT_IRI, new StringValue(annotationSubjectIri.toString()),
        ANNOTATION_PROPERTY_IRI, new StringValue(annotationPropertyIri.toString()),
        LEXICAL_FORM, new StringValue(literalValue.getLiteral()),
        DATATYPE, new StringValue(literalValue.getDatatype().getIRI().toString()),
        LANGUAGE, new StringValue(literalValue.getLang())));
  }

  @Nonnull
  public static Value forAnnotationAssertionAxiom(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId,
                                                  @Nonnull String axiomDigest,
                                                  @Nonnull IRI annotationSubjectIri,
                                                  @Nonnull IRI annotationPropertyIri,
                                                  @Nonnull IRI iriValue) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        DIGEST, new StringValue(axiomDigest),
        ANNOTATION_SUBJECT_IRI, new StringValue(annotationSubjectIri.toString()),
        ANNOTATION_PROPERTY_IRI, new StringValue(annotationPropertyIri.toString()),
        IRI, new StringValue(iriValue.toString())));
  }
}
