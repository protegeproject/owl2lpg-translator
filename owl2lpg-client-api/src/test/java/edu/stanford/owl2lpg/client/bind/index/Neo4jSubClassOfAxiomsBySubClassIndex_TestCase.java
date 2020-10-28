package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.bind.project.index.DefaultIndexLoader;
import edu.stanford.owl2lpg.client.read.NodeMapperModule;
import edu.stanford.owl2lpg.client.read.axiom.DaggerAxiomAccessorComponent;
import edu.stanford.owl2lpg.client.read.handlers.OwlDataFactoryModule;
import edu.stanford.owl2lpg.client.write.GraphWriter;
import edu.stanford.owl2lpg.client.write.QueryBuilderFactory;
import edu.stanford.owl2lpg.client.write.TranslationTranslator;
import edu.stanford.owl2lpg.client.write.handlers.impl.AddAxiomHandler;
import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.BuiltInPrefixDeclarationsModule;
import edu.stanford.owl2lpg.translator.shared.DigestFunctionModule;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.harness.Neo4jBuilders;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubClassOf;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
class Neo4jSubClassOfAxiomsBySubClassIndex_TestCase {

  private final OWLOntologyID ontologyId = new OWLOntologyID(IRI.create("http://example.org/ontology"));

  private final ProjectId projectId = ProjectId.create();

  private final BranchId branchId = BranchId.create();

  private Neo4jSubClassOfAxiomsBySubClassIndex axiomIndex;

  private OWLClass clsA;

  private OWLSubClassOfAxiom axiom1;

  private OWLSubClassOfAxiom axiom2;

  @BeforeEach
  void setUp() {

    var neo4j = Neo4jBuilders.newInProcessBuilder().build();
    var boltUri = neo4j.boltURI();
    var driver = GraphDatabase.driver(boltUri);
    
    var documentIdMap = new DocumentIdMap(driver);
    var ontDocId = documentIdMap.get(projectId, ontologyId);

    var graphWriter = new GraphWriter(driver);

    var axiomTranslator = DaggerTranslatorComponent.builder()
        .builtInPrefixDeclarationsModule(new BuiltInPrefixDeclarationsModule())
        .digestFunctionModule(new DigestFunctionModule())
        .build()
        .getAxiomTranslator();

    var queryBuilderFactory = new QueryBuilderFactory();
    var translationTranslator = new TranslationTranslator(queryBuilderFactory);

    var addAxiomHandler = new AddAxiomHandler(graphWriter, axiomTranslator, translationTranslator);

    var axiomAccessor = DaggerAxiomAccessorComponent.builder()
        .databaseModule(new DatabaseModule(driver))
        .nodeMapperModule(new NodeMapperModule())
        .owlDataFactoryModule(new OwlDataFactoryModule())
        .build()
        .getAxiomAccessor();

    axiomIndex = new Neo4jSubClassOfAxiomsBySubClassIndex(projectId, branchId, documentIdMap, axiomAccessor);

    clsA = Class(IRI.create("http://example.org/A"));
    var clsB = Class(IRI.create("http://example.org/B"));
    var clsC = Class(IRI.create("http://example.org/C"));
    axiom1 = SubClassOf(clsA, clsB);
    axiom2 = SubClassOf(clsA, clsC);

    addAxiomHandler.handle(projectId, branchId, ontDocId, axiom1);
    addAxiomHandler.handle(projectId, branchId, ontDocId, axiom2);

    var indexLoader = new DefaultIndexLoader(driver);
    indexLoader.createIndexes();
  }

  @Test
  void shouldGetSubClassOfAxioms() {
    var subClassOfAxioms = axiomIndex.getSubClassOfAxiomsForSubClass(clsA, ontologyId).collect(ImmutableList.toImmutableList());
    assertEquals(2, subClassOfAxioms.size());
    assertTrue(subClassOfAxioms.contains(axiom1));
    assertTrue(subClassOfAxioms.contains(axiom2));
  }
}