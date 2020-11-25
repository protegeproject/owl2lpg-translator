package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.clsA;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.clsB;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.clsC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubClassOf;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
class Neo4jSubClassOfAxiomsBySubClassIndex_TestCase {

  private Neo4jSubClassOfAxiomsBySubClassIndex axiomIndex;

  private OWLSubClassOfAxiom axiom1, axiom2;

  private AxiomIndexTestHarness testHarness;

  private OntologyDocumentId ontDocIdA, ontDocIdB;

  @BeforeEach
  void setUp() {
    testHarness = AxiomIndexTestHarness.createAndSetUp();
    axiomIndex = new Neo4jSubClassOfAxiomsBySubClassIndex(
        testHarness.getProjectId(),
        testHarness.getBranchId(),
        testHarness.getAxiomAccessor());

    ontDocIdA = testHarness.getOntologyDocumentA();
    ontDocIdB = testHarness.getOntologyDocumentB();

    axiom1 = SubClassOf(clsA, clsB);
    axiom2 = SubClassOf(clsA, clsC);
  }

  @Test
  void shouldNotGetAnyAxioms() {
    var subClassOfAxioms = getAxioms(clsA, ontDocIdA);
    assertThat(subClassOfAxioms.isEmpty(), is(true));
  }

  @Test
  void shouldGetAllSubClassOfAxiomsFromSpecificSubClass() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdA);

    var subClassOfAxioms = getAxioms(clsA, ontDocIdA);

    assertThat(subClassOfAxioms, containsInAnyOrder(axiom1, axiom2));
  }

  @Test
  void shouldOnlyGetSubClassOfAxiomsFromSpecificOntDoc() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdB);

    var subClassOfAxioms = getAxioms(clsA, ontDocIdA);

    assertThat(subClassOfAxioms, containsInAnyOrder(axiom1));
  }

  private ImmutableList<OWLSubClassOfAxiom> getAxioms(OWLClass subClass, OntologyDocumentId ontDocId) {
    return axiomIndex.getSubClassOfAxiomsForSubClass(subClass, ontDocId).collect(ImmutableList.toImmutableList());
  }

  @AfterEach
  void tearDown() {
    testHarness.tearDown();
  }
}