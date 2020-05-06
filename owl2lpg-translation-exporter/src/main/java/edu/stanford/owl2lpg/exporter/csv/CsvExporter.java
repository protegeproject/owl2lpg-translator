package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.Sets;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import edu.stanford.owl2lpg.exporter.csv.beans.*;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import edu.stanford.owl2lpg.versioning.translator.AxiomTranslatorEx;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CsvExporter {

  @Nonnull
  private final AxiomTranslatorEx axiomTranslator;

  @Nonnull
  private final AxiomContext context;

  @Nonnull
  private final OWLOntology ontology;

  @Nonnull
  private final Writer writer;

  private Set<GraphDataElement> allElements = Sets.newHashSet();

  private Set<ProjectNode> projectNodes = Sets.newHashSet();
  private Set<BranchNode> branchNodes = Sets.newHashSet();
  private Set<OntologyDocumentNode> ontoDocNodes = Sets.newHashSet();
  private Set<EntityNode> entityNodes = Sets.newHashSet();
  private Set<IriNode> iriNodes = Sets.newHashSet();
  private Set<LiteralNode> literalNodes = Sets.newHashSet();
  private Set<LanguageTagNode> languageTagNodes = Sets.newHashSet();
  private Set<AnonymousIndividualNode> individualNodes = Sets.newHashSet();
  private Set<CardinalityAxiomNode> cardinalityNodes = Sets.newHashSet();
  private Set<PropertylessNode> propertylessNodes = Sets.newHashSet();
  private Set<PropertylessEdge> propertylessEdges = Sets.newHashSet();

  public CsvExporter(@Nonnull AxiomTranslatorEx axiomTranslator,
                     @Nonnull AxiomContext context,
                     @Nonnull OWLOntology ontology,
                     @Nonnull Writer writer) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.context = checkNotNull(context);
    this.ontology = checkNotNull(ontology);
    this.writer = checkNotNull(writer);
  }

  public void write() throws IOException {
    ontology.getAxioms().stream()
        .map(axiom -> axiomTranslator.translate(context, axiom))
        .forEach(translation -> {
          collectNodes(translation.nodes());
          collectEdges(translation.edges());
        });
    writeCsv();
  }

  private void collectNodes(Stream<Node> nodeStream) {
    nodeStream.forEach(node -> {
      if (isProject(node)) {
        var projectNode = ProjectNode.of(node);
        projectNodes.add(projectNode);
        allElements.add(GraphDataElement.of(projectNode));
      } else if (isBranch(node)) {
        var branchNode = BranchNode.of(node);
        branchNodes.add(branchNode);
        allElements.add(GraphDataElement.of(branchNode));
      } else if (isOntologyDocument(node)) {
        var ontoDocNode = OntologyDocumentNode.of(node);
        ontoDocNodes.add(ontoDocNode);
        allElements.add(GraphDataElement.of(ontoDocNode));
      } else if (isEntity(node)) {
        var entityNode = EntityNode.of(node);
        entityNodes.add(entityNode);
        allElements.add(GraphDataElement.of(entityNode));
      } else if (isIri(node)) {
        var iriNode = IriNode.of(node);
        iriNodes.add(iriNode);
        allElements.add(GraphDataElement.of(iriNode));
      } else if (isLiteral(node)) {
        var literalNode = LiteralNode.create(node);
        literalNodes.add(literalNode);
        allElements.add(GraphDataElement.of(literalNode));
      } else if (isLanguageTag(node)) {
        var languageTagNode = LanguageTagNode.of(node);
        languageTagNodes.add(languageTagNode);
        allElements.add(GraphDataElement.of(languageTagNode));
      } else if (isAnonymousIndividual(node)) {
        var anonymousIndividualNode = AnonymousIndividualNode.of(node);
        individualNodes.add(anonymousIndividualNode);
        allElements.add(GraphDataElement.of(anonymousIndividualNode));
      } else if (isCardinalityAxiom(node)) {
        var cardinalityNode = CardinalityAxiomNode.of(node);
        cardinalityNodes.add(cardinalityNode);
        allElements.add(GraphDataElement.of(cardinalityNode));
      } else {
        var propertylessNode = PropertylessNode.of(node);
        propertylessNodes.add(propertylessNode);
        allElements.add(GraphDataElement.of(propertylessNode));
      }
    });
  }

  private void collectEdges(Stream<Edge> edgeStream) {
    edgeStream.forEach(edge -> {
      var propertylessEdge = PropertylessEdge.of(edge);
      propertylessEdges.add(propertylessEdge);
      allElements.add(GraphDataElement.of(propertylessEdge));
    });
  }

  private boolean isProject(Node node) {
    return AxiomTranslatorEx.NodeLabels.PROJECT.equals(node.getLabels());
  }

  private boolean isBranch(Node node) {
    return AxiomTranslatorEx.NodeLabels.BRANCH.equals(node.getLabels());
  }

  private boolean isOntologyDocument(Node node) {
    return AxiomTranslatorEx.NodeLabels.ONTOLOGY_DOCUMENT.equals(node.getLabels());
  }

  private static boolean isEntity(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.CLASS.equals(nodeLabels) ||
        NodeLabels.OBJECT_PROPERTY.equals(nodeLabels) ||
        NodeLabels.DATA_PROPERTY.equals(nodeLabels) ||
        NodeLabels.ANNOTATION_PROPERTY.equals(nodeLabels) ||
        NodeLabels.NAMED_INDIVIDUAL.equals(nodeLabels) ||
        NodeLabels.DATA_PROPERTY.equals(nodeLabels);
  }

  private static boolean isIri(Node node) {
    return NodeLabels.IRI.equals(node.getLabels());
  }

  private static boolean isLiteral(Node node) {
    return NodeLabels.LITERAL.equals(node.getLabels());
  }

  private static boolean isLanguageTag(Node node) {
    return NodeLabels.LANGUAGE_TAG.equals(node.getLabels());
  }

  private static boolean isAnonymousIndividual(Node node) {
    return NodeLabels.ANONYMOUS_INDIVIDUAL.equals(node.getLabels());
  }

  private static boolean isCardinalityAxiom(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.OBJECT_MIN_CARDINALITY.equals(nodeLabels) ||
        NodeLabels.OBJECT_EXACT_CARDINALITY.equals(nodeLabels) ||
        NodeLabels.OBJECT_MAX_CARDINALITY.equals(nodeLabels) ||
        NodeLabels.DATA_MIN_CARDINALITY.equals(nodeLabels) ||
        NodeLabels.DATA_EXACT_CARDINALITY.equals(nodeLabels) ||
        NodeLabels.DATA_MAX_CARDINALITY.equals(nodeLabels);
  }

  public void flush() throws IOException {
    writer.flush();
  }

  void writeCsv() throws IOException {
    try {
      writeCsv(allElements, GraphDataElement.class, writer);
      writeCsv(projectNodes, ProjectNode.class, "project-node.csv");
      writeCsv(branchNodes, BranchNode.class, "branch-node.csv");
      writeCsv(ontoDocNodes, OntologyDocumentNode.class, "ontology-document-node.csv");
      writeCsv(entityNodes, EntityNode.class, "entity-node.csv");
      writeCsv(iriNodes, IriNode.class, "iri-node.csv");
      writeCsv(literalNodes, LiteralNode.class, "literal-node.csv");
      writeCsv(languageTagNodes, LanguageTagNode.class, "language-tag-node.csv");
      writeCsv(individualNodes, AnonymousIndividualNode.class, "anonymous-individual-node.csv");
      writeCsv(cardinalityNodes, CardinalityAxiomNode.class, "cardinality-axiom-node.csv");
      writeCsv(propertylessNodes, PropertylessNode.class, "propertyless-node.csv");
      writeCsv(propertylessEdges, PropertylessEdge.class, "propertyless-edge.csv");
    } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      throw new IOException(e);
    }
  }

  private <T> void writeCsv(Set<T> input, Class<T> beanClass, String fileName)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    if (!input.isEmpty()) {
      writeCsv(input, beanClass, new PrintWriter(new FileWriter(fileName)));
    }
  }


  private <T> void writeCsv(Set<T> input, Class<T> beanClass, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    if (!input.isEmpty()) {
      var csvBuilder = createCsvBuilder(writer, beanClass);
      csvBuilder.write(input.stream());
      writer.flush();
      writer.close();
    }
  }

  private StatefulBeanToCsv createCsvBuilder(Writer writer, Class<?> cls) {
    return new StatefulBeanToCsvBuilder(writer)
        .withMappingStrategy(new CsvWritingStrategy(cls))
        .build();
  }
}
