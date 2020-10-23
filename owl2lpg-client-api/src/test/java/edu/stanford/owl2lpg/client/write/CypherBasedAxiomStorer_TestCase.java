package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.client.read.NodeMapperModule;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessorComponent;
import edu.stanford.owl2lpg.client.read.axiom.DaggerAxiomAccessorComponent;
import edu.stanford.owl2lpg.client.read.handlers.OwlDataFactoryModule;
import edu.stanford.owl2lpg.client.write.handlers.QueryBuilderFactory;
import edu.stanford.owl2lpg.client.write.handlers.TranslationTranslator;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import edu.stanford.owl2lpg.translator.OntologyContextModule;
import edu.stanford.owl2lpg.translator.TranslatorComponent;
import edu.stanford.owl2lpg.translator.shared.BuiltInPrefixDeclarationsModule;
import edu.stanford.owl2lpg.translator.shared.DigestFunctionModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

public class CypherBasedAxiomStorer_TestCase {

    private ProjectId projectId = ProjectId.create();

    private BranchId branchId = BranchId.create();

    private OntologyDocumentId documentId = OntologyDocumentId.create();

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
                                                           .ontologyContextModule(new OntologyContextModule(projectId,
                                                                                                            branchId,
                                                                                                            documentId))
                                                           .builtInPrefixDeclarationsModule(new BuiltInPrefixDeclarationsModule())
                                                           .digestFunctionModule(new DigestFunctionModule())
                                                           .build();
        axiomTranslator = translatorComponent.getAxiomTranslator();
        // Translator from Translation to Cypher string
        var queryBuilderFactory = new QueryBuilderFactory(projectId, branchId, documentId);
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

        tempCreateIndexes();
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
        var queryStrings = translationTranslator.translateToCypherCreateQuery(translation);
        queryStrings.forEach(graphWriter::execute);
    }

    private void tempCreateIndexes() {
        // Temporary work around until indexes are ensured in the code base
        var cypher = "CREATE CONSTRAINT unique_project_id ON (n:Project) ASSERT n.projectId IS UNIQUE;\n" + "CREATE CONSTRAINT unique_branch_id ON (n:Branch) ASSERT n.branchId IS UNIQUE;\n" + "CREATE CONSTRAINT unique_document_id ON (n:OntologyDocument) ASSERT n.ontologyDocumentId IS UNIQUE;\n" + "CREATE CONSTRAINT unique_iri_iri ON (n:IRI) ASSERT n.iri IS UNIQUE;\n" + "CREATE CONSTRAINT unique_class_iri ON (n:Class) ASSERT n.iri IS UNIQUE;\n" + "CREATE CONSTRAINT unique_data_property_iri ON (n:DataProperty) ASSERT n.iri IS UNIQUE;\n" + "CREATE CONSTRAINT unique_object_property_iri ON (n:ObjectProperty) ASSERT n.iri IS UNIQUE;\n" + "CREATE CONSTRAINT unique_annotation_property_iri ON (n:AnnotationProperty) ASSERT n.iri IS UNIQUE;\n" + "CREATE CONSTRAINT unique_datatype_iri ON (n:Datatype) ASSERT n.iri IS UNIQUE;\n" + "CREATE CONSTRAINT unique_individual_iri ON (n:NamedIndividual) ASSERT n.iri IS UNIQUE;\n" + "CREATE CONSTRAINT unique_class_oboId ON (n:Class) ASSERT n.oboId IS UNIQUE;\n" + "CREATE CONSTRAINT unique_data_property_oboId ON (n:DataProperty) ASSERT n.oboId IS UNIQUE;\n" + "CREATE CONSTRAINT unique_object_property_oboId ON (n:ObjectProperty) ASSERT n.oboId IS UNIQUE;\n" + "CREATE CONSTRAINT unique_annotation_property_oboId ON (n:AnnotationProperty) ASSERT n.oboId IS UNIQUE;\n" + "CREATE CONSTRAINT unique_datatype_oboId ON (n:Datatype) ASSERT n.oboId IS UNIQUE;\n" + "CREATE CONSTRAINT unique_individual_oboId ON (n:NamedIndividual) ASSERT n.oboId IS UNIQUE;\n" + "CREATE CONSTRAINT unique_axiom_digest ON (n:Axiom) ASSERT n.digest IS UNIQUE;\n" + "CREATE INDEX entity_iri_index FOR (n:Entity) ON (n.iri);\n" + "CREATE INDEX entity_iriSuffix_index FOR (n:Entity) ON (n.iriSuffix);\n" + "CREATE INDEX class_iriSuffix_index FOR (n:Class) ON (n.iriSuffix);\n" + "CREATE INDEX data_property_iriSuffix_index FOR (n:DataProperty) ON (n.iriSuffix);\n" + "CREATE INDEX object_property_iriSuffix_index FOR (n:ObjectProperty) ON (n.iriSuffix);\n" + "CREATE INDEX annotation_property_iriSuffix_index FOR (n:AnnotationProperty) ON (n.iriSuffix);\n" + "CREATE INDEX datatype_iriSuffix_index FOR (n:Datatype) ON (n.iriSuffix);\n" + "CREATE INDEX individual_iriSuffix_index FOR (n:NamedIndividual) ON (n.iriSuffix);\n" + "CREATE INDEX entity_oboId_index FOR (n:Entity) ON (n.oboId);\n" + "CREATE INDEX literal_lexicalForm_index FOR (n:Literal) ON (n.lexicalForm);\n" + "CREATE INDEX literal_datatype_index FOR (n:Literal) ON (n.datatype);\n" + "CREATE INDEX literal_language_index FOR (n:Literal) ON (n.language);";
        var queries = cypher.split("\n");
        for(var q : queries) {
            graphWriter.execute(q);
        }
    }
}