package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.bind.project.index.DefaultIndexLoader;
import edu.stanford.owl2lpg.client.read.NodeMapperModule;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.DaggerAxiomAccessorComponent;
import edu.stanford.owl2lpg.client.read.handlers.OwlDataFactoryModule;
import edu.stanford.owl2lpg.client.write.GraphWriter;
import edu.stanford.owl2lpg.client.write.QueryBuilderFactory;
import edu.stanford.owl2lpg.client.write.TranslationTranslator;
import edu.stanford.owl2lpg.client.write.handlers.impl.AddAxiomHandler;
import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import edu.stanford.owl2lpg.translator.shared.*;
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

        axiomAccessor = DaggerAxiomAccessorComponent.builder()
                                                    .databaseModule(new DatabaseModule(driver))
                                                    .nodeMapperModule(new NodeMapperModule())
                                                    .owlDataFactoryModule(new OwlDataFactoryModule())
                                                    .build()
                                                    .getAxiomAccessor();

        ontDocIdA = documentIdMap.get(projectId, ontologyIdA);
        ontDocIdB = documentIdMap.get(projectId, ontologyIdB);



        ensureIndexes();

    }

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

    DocumentIdMap getDocumentMap() {
        return Objects.requireNonNull(documentIdMap, INIT_FAIL);
    }

    AxiomAccessor getAxiomAccessor() {
        return axiomAccessor;
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
