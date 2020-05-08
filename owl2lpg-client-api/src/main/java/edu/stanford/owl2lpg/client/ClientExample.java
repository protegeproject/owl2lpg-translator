package edu.stanford.owl2lpg.client;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.client.read.AccessorFactory;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;
import edu.stanford.owl2lpg.client.read.HierarchyAccessorFactory;
import edu.stanford.owl2lpg.client.read.frame.*;
import edu.stanford.owl2lpg.client.write.AxiomStorer;
import edu.stanford.owl2lpg.client.write.Mode;
import edu.stanford.owl2lpg.client.write.Storer;
import edu.stanford.owl2lpg.client.write.StorerFactory;
import edu.stanford.owl2lpg.translator.NumberIncrementIdProvider;
import edu.stanford.owl2lpg.translator.visitors.AxiomVisitor;
import edu.stanford.owl2lpg.translator.visitors.NodeIdMapper;
import edu.stanford.owl2lpg.translator.visitors.VisitorFactory;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import edu.stanford.owl2lpg.versioning.model.BranchId;
import edu.stanford.owl2lpg.versioning.model.OntologyDocumentId;
import edu.stanford.owl2lpg.versioning.model.ProjectId;
import edu.stanford.owl2lpg.versioning.translator.AxiomTranslatorEx;
import org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.UUID;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClientExample {

  public static void main(String[] args) throws Exception {

    var nodeIdMapper = new NodeIdMapper(new NumberIncrementIdProvider());
    var axiomTranslator = new AxiomTranslatorEx(new AxiomVisitor(new VisitorFactory(nodeIdMapper)));
    var storerMapByMode = Maps.<Mode, Storer>newHashMap();
    storerMapByMode.put(Mode.CYPHER, new AxiomStorer(axiomTranslator));
    var storerMap = Maps.<Class<?>, ImmutableMap<Mode, Storer>>newHashMap();
    storerMap.put(OWLAxiom.class, ImmutableMap.copyOf(storerMapByMode));
    var storerFactory = new StorerFactory(ImmutableMap.copyOf(storerMap));

    var frameAccessorFactories = ImmutableList.<FrameAccessorFactory>of(
        new ClassFrameAccessorFactory(),
        new DataPropertyFrameAccessorFactory(),
        new ObjectPropertyFrameAccessorFactory(),
        new AnnotationPropertyFrameAccessorFactory(),
        new NamedIndividualFrameAccessorFactory());
    var hierarchyAccessorFactories = ImmutableList.<HierarchyAccessorFactory>of();
    var accessorFactory = new AccessorFactory(frameAccessorFactories, hierarchyAccessorFactories);

    var database = Database.builder()
        .setConnection("bolt://localhost:7687", "neo4j", "n304j")
        .setStorers(storerFactory)
        .setAccessors(accessorFactory)
        .connect();

//    var path = "/Users/jhardi/Documents/Ontology/go.owl";
//    var path = "/Users/jhardi/Documents/Ontology/cido.owl";
//    var path = "/Users/jhardi/Documents/Ontology/cido-small.owl";
//    var ontologyFile = new File(path);
//    var ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(ontologyFile);

    var context = AxiomContext.create(
        ProjectId.create(UUID.fromString("c5d78f23-5992-4a9b-9a36-9567059209b0")),
        BranchId.create(UUID.fromString("b6dbf7a0-79cc-4776-afd8-7d1502fc63a6")),
        OntologyDocumentId.create(UUID.fromString("d6d0a42c-fe98-424f-9791-65cea5ef8261")));

//    var timer = Stopwatch.createStarted();
//    var storer = database.getDataStorer();
//    storer.add(context, ontology.getAxioms());
//    storer.close();
//    System.out.println("Storing data took: " + timer.stop());

    var timer = Stopwatch.createStarted();
    var accessor = database.getDataAccessor();
    var frame = accessor.getFrame(context,
        OWLFunctionalSyntaxFactory.Class(
            IRI("http://purl.obolibrary.org/obo/GO_0008150")));
    System.out.println(frame);
    System.out.println("Accessing data took: " + timer.stop());

    System.out.println("Done.");
    database.close();
  }
}
