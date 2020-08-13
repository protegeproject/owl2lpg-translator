package edu.stanford.owl2lpg.ext.plugin.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
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
    return "Analyzer that uses the whitespace tokenizer with ASCII folding, edge n-gram filter, word delimiter graph and lowercase.";
  }

  @Override
  public Analyzer createAnalyzer() {
    try {
      return CustomAnalyzer.builder()
          .withTokenizer(WhitespaceTokenizerFactory.NAME)
          .addTokenFilter(WordDelimiterGraphFilterFactory.NAME,
              "preserveOriginal", "1")
          .addTokenFilter(EdgeNGramFilterFactory.NAME,
              "minGramSize", "3",
              "maxGramSize", "8",
              "preserveOriginal", "true")
          .addTokenFilter(ASCIIFoldingFilterFactory.NAME)
          .addTokenFilter(LowerCaseFilterFactory.NAME)
          .build();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
