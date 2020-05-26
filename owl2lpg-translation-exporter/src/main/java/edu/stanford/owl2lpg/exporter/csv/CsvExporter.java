package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.model.AxiomContext;
import edu.stanford.owl2lpg.translator.VersionedOntologyTranslator;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CsvExporter {

  @Nonnull
  private final VersionedOntologyTranslator axiomTranslator;

  @Nonnull
  private final AxiomContext context;

  @Nonnull
  private final OWLOntology ontology;

  @Nonnull
  private final Path outputDirectory;

  public CsvExporter(@Nonnull VersionedOntologyTranslator axiomTranslator,
                     @Nonnull AxiomContext context,
                     @Nonnull OWLOntology ontology,
                     @Nonnull Path outputDirectory) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.context = checkNotNull(context);
    this.ontology = checkNotNull(ontology);
    this.outputDirectory = checkNotNull(outputDirectory);
  }
}
