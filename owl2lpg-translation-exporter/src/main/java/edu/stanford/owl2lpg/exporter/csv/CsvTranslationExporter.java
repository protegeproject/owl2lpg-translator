package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
// TODO: Rename the class name
public class CsvTranslationExporter {

  public void export(@Nonnull OWLOntology ontology, @Nonnull Path outputDirectory) throws IOException {
    checkNotNull(ontology);
    checkNotNull(outputDirectory);
    var axiomTranslator = DaggerTranslatorComponent.create().getVersionedOntologyTranslator();
    var exporter = new CsvExporter(
        axiomTranslator,
        AxiomContext.create(),
        ontology,
        outputDirectory);
  }
}
