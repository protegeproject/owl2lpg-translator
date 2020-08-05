package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class CsvWriterModule {

  @Nonnull
  private final Path outputPath;

  public CsvWriterModule(@Nonnull Path outputPath) {
    this.outputPath = checkNotNull(outputPath);
  }

  @Provides
  @TranslationSessionScope
  public Neo4jRelationshipsCsvSchema provideEdgeCsvSchema() {
    return new Neo4jRelationshipsCsvSchema();
  }

  @Provides
  @TranslationSessionScope
  public CsvWriter<Node> provideNodeCsvWriter() {
    try {
      var outputFile = new File(outputPath + File.separator + "nodes.csv");
      return new CsvWriter<Node>(
          new CsvMapper(),
          new Neo4jNodeCsvSchema(),
          new BufferedWriter(new FileWriter(outputFile)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides
  @TranslationSessionScope
  public CsvWriter<Edge> provideEdgeCsvWriter() {
    try {
      var outputFile = new File(outputPath + File.separator + "edges.csv");
      return new CsvWriter<Edge>(
          new CsvMapper(),
          new Neo4jRelationshipsCsvSchema(),
          new BufferedWriter(new FileWriter(outputFile)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides
  @TranslationSessionScope
  public NodeTracker provideNodeTracker() {
    return new HashSetNodeTracker();
  }

  @Provides
  @TranslationSessionScope
  public EdgeTracker provideEdgeTracker() {
    return new HashSetEdgeTracker();
  }
}
