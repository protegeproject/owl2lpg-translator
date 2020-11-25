package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import java.util.stream.Stream;

import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.apP;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.apQ;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.apR;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.iriA;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.iriB;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.iriC;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.litStrA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.AnnotationAssertion;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
class Neo4jAnnotationAssertionAxiomsIndex_TestCase {

  private Neo4jAnnotationAssertionAxiomsIndex axiomIndex;

  private OWLAnnotationAssertionAxiom axiom1, axiom2, axiom3;

  private AxiomIndexTestHarness testHarness;

  private OntologyDocumentId ontDocIdA, ontDocIdB;

  @BeforeEach
  void setUp() {
    testHarness = AxiomIndexTestHarness.createAndSetUp();
    axiomIndex = new Neo4jAnnotationAssertionAxiomsIndex(
        testHarness.getProjectId(),
        testHarness.getBranchId(),
        testHarness.getProjectAccessor(),
        testHarness.getAssertionAxiomAccessor());

    ontDocIdA = testHarness.getOntologyDocumentA();
    ontDocIdB = testHarness.getOntologyDocumentB();

    axiom1 = AnnotationAssertion(apP, iriA, litStrA);
    axiom2 = AnnotationAssertion(apQ, iriB, litStrA);
    axiom3 = AnnotationAssertion(apR, iriB, iriC);
  }

  @Test
  void shouldNotGetAnyAxioms() {
    var result = getAxioms(iriA);
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  @Disabled
  void shouldGetAllAnnotationAssertionAxioms() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom3, ontDocIdB);

    var result = getAllAxioms();

    assertThat(result, containsInAnyOrder(axiom1, axiom2, axiom3));
  }

  @Test
  void shouldGetAllAnnotationAssertionAxiomsFromSpecificSubject() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom3, ontDocIdB);

    var result = getAxioms(iriB);

    assertThat(result, containsInAnyOrder(axiom2, axiom3));
  }

  @Test
  void shouldOnlyGetAnnotationAssertionAxiomsFromSpecificSubjectAndProperty() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom3, ontDocIdB);

    var result = getAxioms(iriA, apP);

    assertThat(result, containsInAnyOrder(axiom1));
  }

  private ImmutableList<OWLAnnotationAssertionAxiom> getAllAxioms() {
    return toImmutableList(axiomIndex.getAnnotationAssertionAxioms());
  }

  private ImmutableList<OWLAnnotationAssertionAxiom> getAxioms(IRI annotationSubject) {
    return toImmutableList(axiomIndex.getAnnotationAssertionAxioms(annotationSubject));
  }

  private ImmutableList<OWLAnnotationAssertionAxiom> getAxioms(IRI annotationSubject, OWLAnnotationProperty annotationProperty) {
    return toImmutableList(axiomIndex.getAnnotationAssertionAxioms(annotationSubject, annotationProperty));
  }

  private static ImmutableList<OWLAnnotationAssertionAxiom> toImmutableList(Stream<OWLAnnotationAssertionAxiom> stream) {
    return stream.collect(ImmutableList.toImmutableList());
  }

  @AfterEach
  void tearDown() {
    testHarness.tearDown();
  }
}