package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

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

  private OWLOntologyID ontologyIdA;

  @BeforeEach
  void setUp() {
    testHarness = AxiomIndexTestHarness.createAndSetUp();
    ontologyIdA = testHarness.getOntologyIdA();
    axiomIndex = new Neo4jAnnotationAssertionAxiomsBySubjectIndex(
        testHarness.getProjectId(),
        testHarness.getBranchId(),
        testHarness.getDocumentMap(),
        testHarness.getAssertionAxiomAccessor());

    axiom1 = AnnotationAssertion(apP, iriA, litStrA);
    axiom2 = AnnotationAssertion(apQ, iriA, litInt);
    axiom3 = AnnotationAssertion(apR, iriB, litStrB);
  }

  @Test
  void shouldNotGetAnyAxioms() {
    var subClassOfAxioms = getAxioms(iriA, ontologyIdA);
    assertThat(subClassOfAxioms.isEmpty(), is(true));
  }

  @Test
  void shouldGetAllSubClassOfAxiomsFromOntDoc() {
    testHarness.addAxiomToOntologyDocument_A(axiom1);
    testHarness.addAxiomToOntologyDocument_A(axiom2);
    testHarness.addAxiomToOntologyDocument_A(axiom3);

    var assertionAxioms = getAxioms(iriA, ontologyIdA);

    assertThat(assertionAxioms, containsInAnyOrder(axiom1, axiom2));
  }

  @Test
  void shouldOnlyGetSubClassOfAxiomsFromSpecificOntDoc() {
    testHarness.addAxiomToOntologyDocument_A(axiom1);
    testHarness.addAxiomToOntologyDocument_B(axiom2);

    var assertionAxioms = getAxioms(iriA, ontologyIdA);

    assertThat(assertionAxioms, containsInAnyOrder(axiom1));
  }

  private ImmutableList<OWLAnnotationAssertionAxiom> getAxioms(IRI subject, OWLOntologyID ontologyID) {
    return axiomIndex.getAxiomsForSubject(subject, ontologyID).collect(ImmutableList.toImmutableList());
  }

  @AfterEach
  void tearDown() {
    testHarness.tearDown();
  }
}