package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.bind.graph.NodePropertyIndexBuilder;
import edu.stanford.owl2lpg.client.read.NodeMapperModule;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.DaggerAxiomAccessorComponent;
import edu.stanford.owl2lpg.client.read.handlers.OwlDataFactoryModule;
import edu.stanford.owl2lpg.client.read.ontology.DaggerProjectAccessorComponent;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import edu.stanford.owl2lpg.client.write.GraphWriter;
import edu.stanford.owl2lpg.client.write.QueryBuilderFactory;
import edu.stanford.owl2lpg.client.write.TranslationTranslator;
import edu.stanford.owl2lpg.client.write.handlers.impl.AddAxiomHandler;
import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import edu.stanford.owl2lpg.translator.shared.BuiltInPrefixDeclarationsModule;
import edu.stanford.owl2lpg.translator.shared.DigestFunctionModule;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.harness.Neo4jBuilders;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2020-10-27
 */
public class AxiomIndexTestHarness {

  private final ProjectId projectId;

  private final BranchId branchId;

  private final OntologyDocumentId ontDocIdA, ontDocIdB;

  private AddAxiomHandler addAxiomHandler;

  private ProjectAccessor projectAccessor;

  private AxiomAccessor axiomAccessor;

  private AssertionAxiomAccessor assertionAxiomAccessor;

  private CharacteristicsAxiomAccessor characteristicsAxiomAccessor;

  private Driver driver;

  private AxiomIndexTestHarness() {
    projectId = ProjectId.generate();
    branchId = BranchId.generate();
    ontDocIdA = OntologyDocumentId.generate();
    ontDocIdB = OntologyDocumentId.generate();
  }

  private void setUp() {
    var neo4j = Neo4jBuilders.newInProcessBuilder().build();
    var boltUri = neo4j.boltURI();
    driver = GraphDatabase.driver(boltUri);

    var graphWriter = new GraphWriter(driver);

    var axiomTranslator = DaggerTranslatorComponent.builder()
        .builtInPrefixDeclarationsModule(new BuiltInPrefixDeclarationsModule())
        .digestFunctionModule(new DigestFunctionModule())
        .build()
        .getAxiomTranslator();

    var queryBuilderFactory = new QueryBuilderFactory();
    var translationTranslator = new TranslationTranslator(queryBuilderFactory);

    addAxiomHandler = new AddAxiomHandler(graphWriter, axiomTranslator, translationTranslator);

    var databaseModule = new DatabaseModule(driver);
    var nodeMapperModule = new NodeMapperModule();
    var owlDataFactoryModule = new OwlDataFactoryModule();

    var axiomAccessorComponent = DaggerAxiomAccessorComponent.builder()
        .databaseModule(databaseModule)
        .nodeMapperModule(nodeMapperModule)
        .owlDataFactoryModule(owlDataFactoryModule)
        .build();
    axiomAccessor = axiomAccessorComponent.getAxiomAccessor();
    assertionAxiomAccessor = axiomAccessorComponent.getAssertionAxiomAccessor();
    characteristicsAxiomAccessor = axiomAccessorComponent.getCharacteristicsAxiomAccessor();

    var projectAccessorComponent = DaggerProjectAccessorComponent.builder()
        .databaseModule(databaseModule)
        .nodeMapperModule(nodeMapperModule)
        .owlDataFactoryModule(owlDataFactoryModule)
        .build();
    projectAccessor = projectAccessorComponent.getProjectAccessor();

    ensureIndexes();
  }

  @Nonnull
  public static AxiomIndexTestHarness createAndSetUp() {
    var harness = new AxiomIndexTestHarness();
    harness.setUp();
    return harness;
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
  OntologyDocumentId getOntologyDocumentA() {
    return ontDocIdA;
  }

  @Nonnull
  OntologyDocumentId getOntologyDocumentB() {
    return ontDocIdB;
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

  @Nonnull
  ProjectAccessor getProjectAccessor() {
    return projectAccessor;
  }

  private void ensureIndexes() {
    var indexBuilder = new NodePropertyIndexBuilder(driver);
    indexBuilder.buildIndex();
  }

  void addAxiomToOntologyDocument(OWLAxiom axiom, OntologyDocumentId ontDocId) {
    addAxiomHandler.handle(projectId, branchId, ontDocId, axiom);
  }

  public void tearDown() {
    driver.close();
  }
}
