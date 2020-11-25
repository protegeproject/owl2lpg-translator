package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;

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
class Neo4jAnnotationAssertionAxiomsByValueIndex_TestCase {

  private Neo4jAnnotationAssertionAxiomsByValueIndex axiomIndex;

  private OWLAnnotationAssertionAxiom axiom1, axiom2, axiom3;

  private AxiomIndexTestHarness testHarness;

  private OntologyDocumentId ontDocIdA, ontDocIdB;

  @BeforeEach
  void setUp() {
    testHarness = AxiomIndexTestHarness.createAndSetUp();
    axiomIndex = new Neo4jAnnotationAssertionAxiomsByValueIndex(
        testHarness.getProjectId(),
        testHarness.getBranchId(),
        testHarness.getAnnotationAssertionAxiomAccessor());

    ontDocIdA = testHarness.getOntologyDocumentA();
    ontDocIdB = testHarness.getOntologyDocumentB();

    axiom1 = AnnotationAssertion(apP, iriA, litStrA);
    axiom2 = AnnotationAssertion(apQ, iriB, litStrA);
    axiom3 = AnnotationAssertion(apR, iriB, iriC);
  }

  @Test
  void shouldNotGetAnyAxioms() {
    var result = getAxioms(iriA, ontDocIdA);
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void shouldGetAllAnnotationAssertionAxiomsFromSpecificValue() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom3, ontDocIdA);

    var result = getAxioms(litStrA, ontDocIdA);

    assertThat(result, containsInAnyOrder(axiom1, axiom2));
  }

  @Test
  void shouldOnlyGetAnnotationAssertionAxiomsFromSpecificOntDoc() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdB);

    var result = getAxioms(litStrA, ontDocIdA);

    assertThat(result, containsInAnyOrder(axiom1));
  }

  private ImmutableList<OWLAnnotationAssertionAxiom> getAxioms(OWLAnnotationValue value, OntologyDocumentId ontDocId) {
    return axiomIndex.getAxiomsByValue(value, ontDocId).collect(ImmutableList.toImmutableList());
  }

  @AfterEach
  void tearDown() {
    testHarness.tearDown();
  }
}