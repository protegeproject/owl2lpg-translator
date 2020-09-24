package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.owl2lpg.client.write.GraphWriter;
import edu.stanford.owl2lpg.client.write.Parameters;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.shared.BytesDigester;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectSerializer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AddAnnotationAssertionAxiom {

  private static final String ADD_PLAIN_LITERAL_ANNOTATION_ASSERTION_AXIOM_QUERY_FILE =
      "write/axioms/add-plain-literal-annotation-assertion-axiom.cpy";
  private static final String ADD_TYPED_LITERAL_ANNOTATION_ASSERTION_AXIOM_QUERY_FILE =
      "write/axioms/add-typed-literal-annotation-assertion-axiom.cpy";
  private static final String ADD_IRI_ANNOTATION_ASSERTION_AXIOM_QUERY_FILE =
      "write/axioms/add-iri-annotation-assertion-axiom.cpy";

  private static final String ADD_PLAIN_LITERAL_ANNOTATION_ASSERTION_AXIOM_QUERY =
      read(ADD_PLAIN_LITERAL_ANNOTATION_ASSERTION_AXIOM_QUERY_FILE);
  private static final String ADD_TYPED_LITERAL_ANNOTATION_ASSERTION_AXIOM_QUERY =
      read(ADD_TYPED_LITERAL_ANNOTATION_ASSERTION_AXIOM_QUERY_FILE);
  private static final String ADD_IRI_ANNOTATION_ASSERTION_AXIOM_QUERY =
      read(ADD_IRI_ANNOTATION_ASSERTION_AXIOM_QUERY_FILE);

  @Nonnull
  private final GraphWriter graphWriter;

  @Nonnull
  private final OntologyObjectSerializer ontologyObjectSerializer;

  @Nonnull
  private final BytesDigester bytesDigester;

  @Inject
  public AddAnnotationAssertionAxiom(@Nonnull GraphWriter graphWriter,
                                     @Nonnull OntologyObjectSerializer ontologyObjectSerializer,
                                     @Nonnull BytesDigester bytesDigester) {
    this.graphWriter = checkNotNull(graphWriter);
    this.ontologyObjectSerializer = checkNotNull(ontologyObjectSerializer);
    this.bytesDigester = checkNotNull(bytesDigester);
  }

  public void add(@Nonnull ProjectId projectId,
                  @Nonnull BranchId branchId,
                  @Nonnull OntologyDocumentId ontoDocId,
                  @Nonnull OWLAnnotationAssertionAxiom annotationAssertion) {
    var annotationSubject = annotationAssertion.getSubject();
    var annotationProperty = annotationAssertion.getProperty();
    var annotationValue = annotationAssertion.getValue();

    var axiomDigest = bytesDigester.getDigestString(ontologyObjectSerializer.serialize(annotationAssertion));
    if (annotationValue.isLiteral()) {
      if (annotationValue.asLiteral().isPresent()) {
        var literal = annotationValue.asLiteral().get();
        if (literal.hasLang()) {
          var inputParams = Parameters.forAnnotationAssertionAxiomWithLanguageTag(projectId, branchId, ontoDocId,
              axiomDigest, (IRI) annotationSubject, annotationProperty.getIRI(), literal);
          graphWriter.execute(ADD_PLAIN_LITERAL_ANNOTATION_ASSERTION_AXIOM_QUERY, inputParams);
        } else {
          var inputParams = Parameters.forAnnotationAssertionAxiom(projectId, branchId, ontoDocId,
              axiomDigest, (IRI) annotationSubject, annotationProperty.getIRI(), literal);
          graphWriter.execute(ADD_TYPED_LITERAL_ANNOTATION_ASSERTION_AXIOM_QUERY, inputParams);
        }
      }
    } else if (annotationValue.isIRI()) {
      if (annotationValue.asIRI().isPresent()) {
        var inputParams = Parameters.forAnnotationAssertionAxiom(projectId, branchId, ontoDocId,
            axiomDigest, (IRI) annotationSubject, annotationProperty.getIRI(), annotationValue.asIRI().get());
        graphWriter.execute(ADD_IRI_ANNOTATION_ASSERTION_AXIOM_QUERY, inputParams);
      }
    }
  }
}
