package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;

import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.apP;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.apQ;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.iriA;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.iriB;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.iriP;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.iriQ;
import static edu.stanford.owl2lpg.client.bind.index.TestOwlObjects.litStrA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.AnnotationAssertion;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.AnnotationPropertyDomain;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.AnnotationPropertyRange;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
class Neo4jAnnotationAxiomsByIriReferenceIndex_TestCase {

  private Neo4jAnnotationAxiomsByIriReferenceIndex axiomIndex;

  private OWLAnnotationAxiom axiom1, axiom2, axiom3;

  private AxiomIndexTestHarness testHarness;

  private OntologyDocumentId ontDocIdA, ontDocIdB;

  @BeforeEach
  void setUp() {
    testHarness = AxiomIndexTestHarness.createAndSetUp();
    axiomIndex = new Neo4jAnnotationAxiomsByIriReferenceIndex(
        testHarness.getProjectId(),
        testHarness.getBranchId(),
        testHarness.getAxiomAccessor());

    ontDocIdA = testHarness.getOntologyDocumentA();
    ontDocIdB = testHarness.getOntologyDocumentB();

    axiom1 = AnnotationAssertion(apP, iriA, litStrA);
    axiom2 = AnnotationPropertyDomain(apQ, iriB);
    axiom3 = AnnotationPropertyRange(apQ, iriB);
  }

  @Test
  void shouldNotGetAnyAxioms() {
    var result = getAxioms(iriA, ontDocIdA);
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  void shouldGetAllAnnotationAxiomsFromSpecificIri() {
    testHarness.addAxiomToOntologyDocument(axiom1, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom3, ontDocIdA);

    var result = getAxioms(iriP, ontDocIdA);

    assertThat(result, containsInAnyOrder(axiom1));
  }

  @Test
  void shouldOnlyGetAnnotationAxiomsFromSpecificOntDoc() {
    testHarness.addAxiomToOntologyDocument(axiom2, ontDocIdA);
    testHarness.addAxiomToOntologyDocument(axiom3, ontDocIdB);

    var result = getAxioms(iriQ, ontDocIdB);

    assertThat(result, containsInAnyOrder(axiom3));
  }

  private ImmutableList<OWLAnnotationAxiom> getAxioms(IRI iri, OntologyDocumentId ontDocId) {
    return axiomIndex.getReferencingAxioms(iri, ontDocId).collect(ImmutableList.toImmutableList());
  }

  @AfterEach
  void tearDown() {
    testHarness.tearDown();
  }
}