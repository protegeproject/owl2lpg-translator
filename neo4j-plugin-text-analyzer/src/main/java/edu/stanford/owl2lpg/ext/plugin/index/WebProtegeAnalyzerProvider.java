package edu.stanford.owl2lpg.ext.plugin.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.neo4j.graphdb.schema.AnalyzerProvider;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class WebProtegeAnalyzerProvider extends AnalyzerProvider {

  public WebProtegeAnalyzerProvider() {
    super("webprotege-analyzer");
  }

  @Override
  public String description() {
    return "Analyzer that uses a standard tokenizer with ASCII folding, lowercase, and edge n-gram filters";
  }

  @Override
  public Analyzer createAnalyzer() {
    try {
      return CustomAnalyzer.builder()
          .withTokenizer(StandardTokenizerFactory.NAME)
          .addTokenFilter(ASCIIFoldingFilterFactory.NAME)
          .addTokenFilter(LowerCaseFilterFactory.NAME)
          .addTokenFilter(EdgeNGramFilterFactory.NAME,
              "minGramSize", "2",
              "maxGramSize", "10",
              "preserveOriginal", "true")
          .build();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
