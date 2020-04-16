package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CsvExporter {

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final ImmutableSet<OWLAxiom> axioms;

  @Nonnull
  private final Writer writer;

  private List<GraphDataElement> allElements = Lists.newArrayList();

  public CsvExporter(@Nonnull AxiomTranslator axiomTranslator,
                     @Nonnull ImmutableSet<OWLAxiom> axioms,
                     @Nonnull Writer writer) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.axioms = checkNotNull(axioms);
    this.writer = checkNotNull(writer);
  }

  public void write() throws IOException {
    axioms.forEach(axiom -> {
      var translation = axiomTranslator.translate(axiom);
      collectNodes(translation.nodes());
      collectEdges(translation.edges());
    });
    write(allElements);
  }

  private void collectNodes(Stream<Node> nodeStream) {
    nodeStream.forEach(node -> {
      if (isEntity(node)) {
        allElements.add(GraphDataElement.createEntityNode(
            printNodeId(node.getNodeId()),
            Objects.requireNonNull(node.getProperties().get(PropertyNames.IRI)),
            node.getLabels()));
      } else if (isIri(node)) {
        allElements.add(GraphDataElement.createIriNode(
            printNodeId(node.getNodeId()),
            node.getProperties().get(PropertyNames.IRI),
            node.getLabels()));
      } else if (isLiteral(node)) {
        allElements.add(GraphDataElement.createLiteralNode(
            printNodeId(node.getNodeId()),
            node.getProperties().get(PropertyNames.LEXICAL_FORM),
            node.getLabels()));
      } else if (isLanguageTag(node)) {
        allElements.add(GraphDataElement.createLanguageTagNode(
            printNodeId(node.getNodeId()),
            node.getProperties().get(PropertyNames.LANGUAGE),
            node.getLabels()));
      } else if (isAnonymousIndividual(node)) {
        allElements.add(GraphDataElement.createAnonymousIndividualNode(
            printNodeId(node.getNodeId()),
            node.getProperties().get(PropertyNames.NODE_ID),
            node.getLabels()));
      } else if (isCardinalityAxiom(node)) {
        allElements.add(GraphDataElement.creatCardinalityAxiomeNode(
            printNodeId(node.getNodeId()),
            node.getProperties().get(PropertyNames.CARDINALITY),
            node.getLabels()));
      } else {
        allElements.add(GraphDataElement.createNode(
            printNodeId(node.getNodeId()),
            node.getLabels()));
      }
    });
  }

  private void collectEdges(Stream<Edge> edgeStream) {
    edgeStream.forEach(edge ->
        allElements.add(GraphDataElement.createEdge(
            printNodeId(edge.getFromNode().getNodeId()),
            printNodeId(edge.getToNode().getNodeId()),
            edge.getLabel()))
    );
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

  void write(@Nullable List<GraphDataElement> elements) throws IOException {
    if (elements == null) {
      return;
    }
    try {
      var beanToCsv = new StatefulBeanToCsvBuilder(writer)
          .withMappingStrategy(getCustomMappingStrategy())
          .build();
      beanToCsv.write(elements);
    } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      throw new IOException(e);
    }
  }

  private HeaderColumnNameTranslateMappingStrategy<GraphDataElement> getCustomMappingStrategy() {
    var mappingStrategy = new HeaderColumnByAnnotationStrategy();
    var columnNameComparator = new ClassFieldOrderComparator(GraphDataElement.class);
    mappingStrategy.setType(GraphDataElement.class);
    mappingStrategy.setColumnOrderOnWrite(columnNameComparator);
    return mappingStrategy;
  }

  /*
   * Code taken from:
   * https://stackoverflow.com/questions/56168094/why-opencsv-capitalizing-csv-
   * headers-while-writing-to-file
   */
  private static class HeaderColumnByAnnotationStrategy<T> extends HeaderColumnNameTranslateMappingStrategy<T> {
    @Override
    public void setType(Class<? extends T> cls) {
      super.setType(cls);
      var map = Maps.<String, String>newHashMap();
      Arrays.stream(cls.getDeclaredFields())
          .forEach(field -> {
            var a1 = field.getAnnotation(CsvBindByName.class);
            if (a1 != null) {
              map.put(field.getName(), a1.column());
            }
            var a2 = field.getAnnotation(CsvBindAndSplitByName.class);
            if (a2 != null) {
              map.put(field.getName(), a2.column());
            }
          });
      setColumnMapping(map);
    }

    @Override
    public String getColumnName(int col) {
      return headerIndex.getByPosition(col);
    }

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
      var result = super.generateHeader(bean);
      for (int i = 0; i < result.length; i++) {
        result[i] = getColumnMapping().get(getColumnName(i));
      }
      return result;
    }
  }

  /**
   * Code taken from:
   * https://stackoverflow.com/questions/45203867/opencsv-how-to-create-csv-
   * file-from-pojo-with-custom-column-headers-and-custom
   */
  private static class ClassFieldOrderComparator implements Comparator<String> {

    private List<String> columnNamesInOrder;

    public ClassFieldOrderComparator(Class<?> clazz) {
      columnNamesInOrder = Arrays.stream(clazz.getDeclaredFields())
          .filter(field -> field.getAnnotation(CsvBindByName.class) != null ||
              field.getAnnotation(CsvBindAndSplitByName.class) != null)
          .map(field -> field.getName().toUpperCase())
          .collect(Collectors.toList());
    }

    @Override
    public int compare(String o1, String o2) {
      var fieldIndexo1 = columnNamesInOrder.indexOf(o1);
      var fieldIndexo2 = columnNamesInOrder.indexOf(o2);
      return Integer.compare(fieldIndexo1, fieldIndexo2);
    }
  }
}
