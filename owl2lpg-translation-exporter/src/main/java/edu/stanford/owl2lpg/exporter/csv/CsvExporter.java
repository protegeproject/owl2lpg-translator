package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.Sets;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import edu.stanford.owl2lpg.exporter.csv.beans.*;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.OntologyTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CsvExporter {

  @Nonnull
  private final OntologyTranslator ontologyTranslator;

  @Nonnull
  private final OWLOntology ontology;

  @Nonnull
  private final Writer writer;

  private Set<GraphDataElement> allElements = Sets.newHashSet();

  private Set<EntityNode> entityNodes = Sets.newHashSet();
  private Set<IriNode> iriNodes = Sets.newHashSet();
  private Set<LiteralNode> literalNodes = Sets.newHashSet();
  private Set<LanguageTagNode> languageTagNodes = Sets.newHashSet();
  private Set<AnonymousIndividualNode> individualNodes = Sets.newHashSet();
  private Set<CardinalityAxiomNode> cardinalityNodes = Sets.newHashSet();
  private Set<PropertylessNode> propertylessNodes = Sets.newHashSet();
  private Set<PropertylessEdge> propertylessEdges = Sets.newHashSet();

  public CsvExporter(@Nonnull OntologyTranslator ontologyTranslator,
                     @Nonnull OWLOntology ontology,
                     @Nonnull Writer writer) {
    this.ontologyTranslator = checkNotNull(ontologyTranslator);
    this.ontology = checkNotNull(ontology);
    this.writer = checkNotNull(writer);
  }

  public void write() throws IOException {
    Translation ontologyTranslation = ontologyTranslator.translate(ontology);
    collectNodes(ontologyTranslation.nodes());
    collectEdges(ontologyTranslation.edges());
    writeCsv();
  }

  private void collectNodes(Stream<Node> nodeStream) {
    nodeStream.forEach(node -> {
      if (isEntity(node)) {
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

  private static String printNodeId(int nodeId) {
    return format("NodeId_%d", nodeId);
  }

  public void flush() throws IOException {
    writer.flush();
  }

  void writeCsv() throws IOException {
    try {
      writeFatCsv(allElements, writer);
      var entityNodeWriter = createPrinterWriter("entity-node.csv");
      writeEntityNodeCsv(entityNodes, entityNodeWriter);
      var iriNodeWriter = createPrinterWriter("iri-node.csv");
      writeIriNodeCsv(iriNodes, iriNodeWriter);
      var literalNodeWriter = createPrinterWriter("literal-node.csv");
      writeLiteralNodeCsv(literalNodes, literalNodeWriter);
      var languageTagWriter = createPrinterWriter("language-tag-node.csv");
      writeLanguageTagNodeCsv(languageTagNodes, languageTagWriter);
      var anonymousIndividualNodeWriter = createPrinterWriter("anonymous-individual-node.csv");
      writeAnonymousIndividualNodeCsv(individualNodes, anonymousIndividualNodeWriter);
      var cardinalityAxiomNodeWriter = createPrinterWriter("cardinality-axiom-node.csv");
      writeCardinalityAxiomNodeCsv(cardinalityNodes, cardinalityAxiomNodeWriter);
      var propertylessNodeWriter = createPrinterWriter("propertyless-node.csv");
      writePropertylessNodeCsv(propertylessNodes, propertylessNodeWriter);
      var propertylessEdgeWriter = createPrinterWriter("propertyless-edge.csv");
      writePropertylessEdgeCsv(propertylessEdges, propertylessEdgeWriter);
    } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      throw new IOException(e);
    }
  }

  private PrintWriter createPrinterWriter(String filename) throws IOException {
    return new PrintWriter(new FileWriter(filename));
  }

  private void writeFatCsv(Set<GraphDataElement> input, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    createCsvBuilder(writer, GraphDataElement.class).write(input.stream());
    writer.flush();
    writer.close();
  }

  private void writeEntityNodeCsv(Set<EntityNode> input, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    createCsvBuilder(writer, EntityNode.class).write(input.stream());
    writer.flush();
    writer.close();
  }

  private void writeIriNodeCsv(Set<IriNode> input, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    createCsvBuilder(writer, IriNode.class).write(input.stream());
    writer.flush();
    writer.close();
  }

  private void writeLiteralNodeCsv(Set<LiteralNode> input, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    createCsvBuilder(writer, LiteralNode.class).write(input.stream());
    writer.flush();
    writer.close();
  }

  private void writeLanguageTagNodeCsv(Set<LanguageTagNode> input, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    createCsvBuilder(writer, LanguageTagNode.class).write(input.stream());
    writer.flush();
    writer.close();
  }

  private void writeAnonymousIndividualNodeCsv(Set<AnonymousIndividualNode> input, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    createCsvBuilder(writer, AnonymousIndividualNode.class).write(input.stream());
    writer.flush();
    writer.close();
  }

  private void writeCardinalityAxiomNodeCsv(Set<CardinalityAxiomNode> input, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    createCsvBuilder(writer, CardinalityAxiomNode.class).write(input.stream());
    writer.flush();
    writer.close();
  }

  private void writePropertylessNodeCsv(Set<PropertylessNode> input, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    createCsvBuilder(writer, PropertylessNode.class).write(input.stream());
    writer.flush();
    writer.close();
  }

  private void writePropertylessEdgeCsv(Set<PropertylessEdge> input, Writer writer)
      throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
    createCsvBuilder(writer, PropertylessEdge.class).write(input.stream());
    writer.flush();
    writer.close();
  }

  private StatefulBeanToCsv createCsvBuilder(Writer writer, Class<?> cls) {
    return new StatefulBeanToCsvBuilder(writer)
        .withMappingStrategy(getCustomMappingStrategy(cls))
        .build();
  }

  private CsvWritingStrategy getCustomMappingStrategy(Class<?> cls) {
    return new CsvWritingStrategy(cls);
  }
}
