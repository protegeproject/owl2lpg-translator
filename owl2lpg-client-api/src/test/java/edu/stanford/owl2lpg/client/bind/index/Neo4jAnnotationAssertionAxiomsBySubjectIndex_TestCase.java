package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

import static edu.stanford.owl2lpg.client.bind.index.OwlObjects.apP;
import static edu.stanford.owl2lpg.client.bind.index.OwlObjects.apQ;
import static edu.stanford.owl2lpg.client.bind.index.OwlObjects.apR;
import static edu.stanford.owl2lpg.client.bind.index.OwlObjects.iriA;
import static edu.stanford.owl2lpg.client.bind.index.OwlObjects.iriB;
import static edu.stanford.owl2lpg.client.bind.index.OwlObjects.litInt;
import static edu.stanford.owl2lpg.client.bind.index.OwlObjects.litStrA;
import static edu.stanford.owl2lpg.client.bind.index.OwlObjects.litStrB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.AnnotationAssertion;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
class Neo4jAnnotationAssertionAxiomsBySubjectIndex_TestCase {

  private Neo4jAnnotationAssertionAxiomsBySubjectIndex axiomIndex;

  private OWLAnnotationAssertionAxiom axiom1, axiom2, axiom3;

  private AxiomIndexTestHarness testHarness;

  private OntologyDocumentId ontDocIdA, ontDocIdB;

  @BeforeEach
  void setUp() {
    testHarness = AxiomIndexTestHarness.createAndSetUp();
    axiomIndex = new Neo4jAnnotationAssertionAxiomsBySubjectIndex(
        testHarness.getProjectId(),
        testHarness.getBranchId(),
        testHarness.getAssertionAxiomAccessor());

    ontDocIdA = testHarness.getOntologyDocumentA();
    ontDocIdB = testHarness.getOntologyDocumentB();

    axiom1 = AnnotationAssertion(apP, iriA, litStrA);
    axiom2 = AnnotationAssertion(apQ, iriA, litInt);
    axiom3 = AnnotationAssertion(apR, iriB, litStrB);
  }

  @Test
  void shouldNotGetAnyAxioms() {
    var assertionAxioms = getAnnotationAssertionAxioms(iriA, ontDocIdA);
    assertThat(assertionAxioms.isEmpty(), is(true));
  }

  @Test
  void shouldGetAllSubClassOfAxiomsFromOntDoc() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom3, ontDocIdA);

    var assertionAxioms = getAnnotationAssertionAxioms(iriA, ontDocIdA);

    assertThat(assertionAxioms, containsInAnyOrder(axiom1, axiom2));
  }

  @Test
  void shouldOnlyGetSubClassOfAxiomsFromSpecificOntDoc() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdB);

    var assertionAxioms = getAnnotationAssertionAxioms(iriA, ontDocIdA);

    assertThat(assertionAxioms, containsInAnyOrder(axiom1));
  }

  private ImmutableList<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(IRI subject, OntologyDocumentId ontDocId) {
    return axiomIndex.getAxiomsForSubject(subject, ontDocId).collect(ImmutableList.toImmutableList());
  }

  @AfterEach
  void tearDown() {
    testHarness.tearDown();
  }
}