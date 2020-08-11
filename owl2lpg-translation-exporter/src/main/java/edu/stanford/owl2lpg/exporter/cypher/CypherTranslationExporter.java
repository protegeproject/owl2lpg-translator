package edu.stanford.owl2lpg.exporter.cypher;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import edu.stanford.owl2lpg.translator.VersioningContextModule;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

public class CypherTranslationExporter {

  public void export(@Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId ontologyDocumentId,
                     @Nonnull Path ontologyFilePath,
                     @Nonnull Path outputFilePath) throws IOException {
    checkNotNull(ontologyFilePath);
    checkNotNull(outputFilePath);
    try {
      var ontologyFile = ontologyFilePath.toFile();
      var ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(ontologyFile);
      export(projectId, branchId, ontologyDocumentId, ontology, outputFilePath);
    } catch (OWLOntologyCreationException e) {
      throw new IOException(e);
    }
  }

  public void export(@Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId ontologyDocumentId,
                     @Nonnull OWLOntology ontology,
                     @Nonnull Path outputFilePath) {
    checkNotNull(ontology);
    checkNotNull(outputFilePath);
    var versioningContextModule = new VersioningContextModule(projectId, branchId, ontologyDocumentId);
    var ontologyTranslator = DaggerTranslatorComponent.builder()
        .versioningContextModule(versioningContextModule)
        .build()
        .getOntologyTranslator();
    var exporter = new CypherExporter(ontology, ontologyTranslator, outputFilePath);
    exporter.write();
  }
}
