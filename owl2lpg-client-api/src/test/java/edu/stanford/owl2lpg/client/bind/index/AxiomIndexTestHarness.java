package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.bind.project.index.DefaultIndexLoader;
import edu.stanford.owl2lpg.client.read.NodeMapperModule;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
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
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.harness.Neo4jBuilders;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2020-10-27
 */
public class AxiomIndexTestHarness {

  private static final String INIT_FAIL = "setUp has not been called";

  private final OWLOntologyID ontologyIdA;

  private final OWLOntologyID ontologyIdB;

  private final ProjectId projectId;

  private final BranchId branchId;

  private DocumentIdMap documentIdMap;

  private OntologyDocumentId ontDocIdA, ontDocIdB;

  private AddAxiomHandler addAxiomHandler;

  private AxiomAccessor axiomAccessor;

  private AssertionAxiomAccessor assertionAxiomAccessor;

  private CharacteristicsAxiomAccessor characteristicsAxiomAccessor;

  private Driver driver;

  private AxiomIndexTestHarness() {
    ontologyIdA = new OWLOntologyID(IRI.create("http://example.org/ontologyA"));
    ontologyIdB = new OWLOntologyID(IRI.create("http://example.org/ontologyB"));
    projectId = ProjectId.create();
    branchId = BranchId.create();
  }

  private void setUp() {

    var neo4j = Neo4jBuilders.newInProcessBuilder().build();
    var boltUri = neo4j.boltURI();
    driver = GraphDatabase.driver(boltUri);

    this.documentIdMap = new DocumentIdMap(driver);

    var graphWriter = new GraphWriter(driver);

    var axiomTranslator = DaggerTranslatorComponent.builder()
        .builtInPrefixDeclarationsModule(new BuiltInPrefixDeclarationsModule())
        .digestFunctionModule(new DigestFunctionModule())
        .build()
        .getAxiomTranslator();

    var queryBuilderFactory = new QueryBuilderFactory();
    var translationTranslator = new TranslationTranslator(queryBuilderFactory);

    addAxiomHandler = new AddAxiomHandler(graphWriter, axiomTranslator, translationTranslator);

    var axiomAccessorComponent = DaggerAxiomAccessorComponent.builder()
        .databaseModule(new DatabaseModule(driver))
        .nodeMapperModule(new NodeMapperModule())
        .owlDataFactoryModule(new OwlDataFactoryModule())
        .build();
    axiomAccessor = axiomAccessorComponent.getAxiomAccessor();
    assertionAxiomAccessor = axiomAccessorComponent.getAssertionAxiomAccessor();
    characteristicsAxiomAccessor = axiomAccessorComponent.getCharacteristicsAxiomAccessor();

    ontDocIdA = documentIdMap.get(projectId, ontologyIdA);
    ontDocIdB = documentIdMap.get(projectId, ontologyIdB);

    ensureIndexes();

  }

  @Nonnull
  public static AxiomIndexTestHarness createAndSetUp() {
    var harness = new AxiomIndexTestHarness();
    harness.setUp();
    return harness;
  }

  @Nonnull
  OWLOntologyID getOntologyIdA() {
    return Objects.requireNonNull(ontologyIdA, INIT_FAIL);
  }

  @Nonnull
  ProjectId getProjectId() {
    return projectId;
  }

  @Nonnull
  BranchId getBranchId() {
    return branchId;
  }

  @Nonnull
  DocumentIdMap getDocumentMap() {
    return Objects.requireNonNull(documentIdMap, INIT_FAIL);
  }

  @Nonnull
  AxiomAccessor getAxiomAccessor() {
    return axiomAccessor;
  }

  @Nonnull
  AssertionAxiomAccessor getAssertionAxiomAccessor() {
    return assertionAxiomAccessor;
  }

  @Nonnull
  CharacteristicsAxiomAccessor getCharacteristicsAxiomAccessor() {
    return characteristicsAxiomAccessor;
  }

  private void ensureIndexes() {
    var indexLoader = new DefaultIndexLoader(driver);
    indexLoader.createIndexes();
  }

  void addAxiomToOntologyDocument_A(OWLAxiom axiom) {
    addAxiomHandler.handle(projectId, branchId, ontDocIdA, axiom);
  }

  void addAxiomToOntologyDocument_B(OWLAxiom axiom) {
    addAxiomHandler.handle(projectId, branchId, ontDocIdB, axiom);
  }

  public void tearDown() {
    driver.close();
  }
}
