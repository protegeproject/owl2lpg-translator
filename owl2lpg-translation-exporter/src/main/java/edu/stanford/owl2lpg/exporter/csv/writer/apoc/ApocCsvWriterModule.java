package edu.stanford.owl2lpg.exporter.csv.writer.apoc;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvWriter;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class ApocCsvWriterModule {

  @Nonnull
  private final Path importDirectory;

  public ApocCsvWriterModule(@Nonnull Path importDirectory) {
    this.importDirectory = checkNotNull(importDirectory);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideProjectNodeWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new ProjectNodeSchema(),
        newBufferedWriter("project.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideBranchNodeWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new BranchNodeSchema(),
        newBufferedWriter("branch.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideOntologyDocumentNodeWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new OntologyDocumentNodeSchema(),
        newBufferedWriter("ontology-documents.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideAxiomNodeWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new AxiomNodeSchema(),
        newBufferedWriter("axioms.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideCardinalityNodeWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new CardinalityNodeSchema(),
        newBufferedWriter("cardinality-expressions.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideEntityNodeWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new EntityNodeSchema(),
        newBufferedWriter("entities.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideAnonymousIndividualNodeWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new AnonymousIndividualNodeSchema(),
        newBufferedWriter("anonymous-individuals.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideLiteralNodeWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new LiteralNodeSchema(),
        newBufferedWriter("literals.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideIriNodeWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new IriNodeSchema(),
        newBufferedWriter("iris.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideOtherNodesWriter() {
    return new CsvWriter<Node>(
        new CsvMapper(),
        new DefaultNodeSchema(),
        newBufferedWriter("other-nodes.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Edge> provideRelatedToEdgeCsvWriter() {
    return new CsvWriter<Edge>(
        new CsvMapper(),
        new RelatedToEdgeSchema(),
        newBufferedWriter("related-to-edges.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Edge> provideNextEdgeCsvWriter() {
    return new CsvWriter<Edge>(
        new CsvMapper(),
        new NextEdgeSchema(),
        newBufferedWriter("next-edges.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Edge> provideStructuralEdgeCsvWriter() {
    return new CsvWriter<Edge>(
        new CsvMapper(),
        new StructuralEdgeSchema(),
        newBufferedWriter("structural-edges.csv"), true);
  }

  @Provides
  @IntoSet
  public CsvWriter<Edge> provideAugmentingEdgeCsvWriter() {
    return new CsvWriter<Edge>(
        new CsvMapper(),
        new AugmentingEdgeSchema(),
        newBufferedWriter("augmenting-edges.csv"), true);
  }

  @Nonnull
  private BufferedWriter newBufferedWriter(String fileName) {
    try {
      var outputFile = resolveOutputPath(fileName);
      return new BufferedWriter(new FileWriter(outputFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  private File resolveOutputPath(String fileName) {
    createDirIfNotExist(importDirectory);
    return importDirectory.resolve(fileName).toFile();
  }

  private static void createDirIfNotExist(@NotNull Path importDirectory) {
    if (!Files.exists(importDirectory)) {
      try {
        Files.createDirectory(importDirectory);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
