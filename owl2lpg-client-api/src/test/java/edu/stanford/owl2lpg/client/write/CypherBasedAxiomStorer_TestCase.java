package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.bind.project.index.DefaultIndexLoader;
import edu.stanford.owl2lpg.client.bind.project.index.IndexLoader;
import edu.stanford.owl2lpg.client.read.NodeMapperModule;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.DaggerAxiomAccessorComponent;
import edu.stanford.owl2lpg.client.read.handlers.OwlDataFactoryModule;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.BuiltInPrefixDeclarationsModule;
import edu.stanford.owl2lpg.translator.shared.DigestFunctionModule;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.harness.Neo4jBuilders;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubClassOf;

public class CypherBasedAxiomStorer_TestCase {

  private final ProjectId projectId = ProjectId.create();

  private final BranchId branchId = BranchId.create();

  private final OntologyDocumentId documentId = OntologyDocumentId.create();

  private IndexLoader indexLoader;

  private AxiomAccessor axiomAccessor;

  private TranslationTranslator translationTranslator;

  private GraphWriter graphWriter;

  private AxiomTranslator axiomTranslator;

  private OWLAxiom axiom;

  @BeforeEach
  void setUp() {
    var neo4j = Neo4jBuilders.newInProcessBuilder().build();
    var boltUri = neo4j.boltURI();
    var driver = GraphDatabase.driver(boltUri);

    // Translator from OWLObject to Translation
    var translatorComponent = DaggerTranslatorComponent.builder()
        .builtInPrefixDeclarationsModule(new BuiltInPrefixDeclarationsModule())
        .digestFunctionModule(new DigestFunctionModule())
        .build();
    axiomTranslator = translatorComponent.getAxiomTranslator();

    // Translator from Translation to Cypher string
    var queryBuilderFactory = new QueryBuilderFactory();
    translationTranslator = new TranslationTranslator(queryBuilderFactory);

    // The writer to execute Cypher query
    graphWriter = new GraphWriter(driver);

    // Accessor to get axioms from Neo4j
    var axiomAccessorComponent = DaggerAxiomAccessorComponent.builder()
        .databaseModule(new DatabaseModule(driver))
        .nodeMapperModule(new NodeMapperModule())
        .owlDataFactoryModule(new OwlDataFactoryModule())
        .build();
    axiomAccessor = axiomAccessorComponent.getAxiomAccessor();

    var clsA = Class(IRI.create("http://example.org/A"));
    var clsB = Class(IRI.create("http://example.org/B"));
    axiom = SubClassOf(clsA, clsB);

    indexLoader = new DefaultIndexLoader(driver);
    indexLoader.createIndexes();
  }


  @Test
  void shouldStoreSubClassOf() {
    storeAndRetrieveAxiom(axiom);
  }

  @Test
  void shouldAcceptDuplicate() {
    storeAxiom(axiom);
    storeAxiom(axiom);
  }


  private void storeAndRetrieveAxiom(@Nonnull OWLAxiom axiom) {
    storeAxiom(axiom);
    var storedAxioms = axiomAccessor.getAllAxioms(projectId, branchId, documentId);
    assertTrue(storedAxioms.contains(axiom));
  }

  private void storeAxiom(@Nonnull OWLAxiom axiom) {
    var translation = axiomTranslator.translate(axiom);
    var queryStrings = translationTranslator.translateToCypherCreateQuery(projectId, branchId, documentId, translation);
    queryStrings.forEach(graphWriter::execute);
  }
}