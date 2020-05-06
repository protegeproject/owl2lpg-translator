package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.exporter.AbstractTranslationExporter;
import edu.stanford.owl2lpg.translator.NumberIncrementIdProvider;
import edu.stanford.owl2lpg.translator.visitors.AxiomVisitor;
import edu.stanford.owl2lpg.translator.visitors.NodeIdMapper;
import edu.stanford.owl2lpg.translator.visitors.VisitorFactory;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import edu.stanford.owl2lpg.versioning.translator.AxiomTranslatorEx;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CsvTranslationExporter extends AbstractTranslationExporter {

  public void export(@Nonnull File ontologyFile, @Nonnull Writer writer) throws IOException {
    checkNotNull(ontologyFile);
    checkNotNull(writer);
    try {
      var ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(ontologyFile);
      export(ontology, writer);
    } catch (OWLOntologyCreationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void export(@Nonnull OWLOntology ontology, @Nonnull Writer writer) throws IOException {
    checkNotNull(ontology);
    checkNotNull(writer);
    // TODO: Use injection
    var nodeIdMapper = new NodeIdMapper(new NumberIncrementIdProvider());
    var axiomTranslator = new AxiomTranslatorEx(new AxiomVisitor(new VisitorFactory(nodeIdMapper)));
    var exporter = new CsvExporter(
        axiomTranslator,
        AxiomContext.create(),
        ontology,
        writer);
    exporter.write();
    exporter.flush();
  }
}
