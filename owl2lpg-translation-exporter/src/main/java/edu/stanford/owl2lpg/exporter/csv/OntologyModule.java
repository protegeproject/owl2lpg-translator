package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class OntologyModule {

    @Nonnull
    private final File ontologyFile;

    @Nonnull
    private final Optional<String> ontologyDocumentId;

    public OntologyModule(@Nonnull File ontologyFile) {
        this(ontologyFile, Optional.empty());
    }

    public OntologyModule(@Nonnull File ontologyFile,
                          @Nonnull String ontologyIdString) {
        this(ontologyFile, Optional.of(ontologyIdString));
    }

    private OntologyModule(@Nonnull File ontologyFile,
                           @Nonnull Optional<String> ontologyIdString) {
        this.ontologyFile = checkNotNull(ontologyFile);
        this.ontologyDocumentId = checkNotNull(ontologyIdString);
    }

    @Provides
    @TranslationSessionScope
    public ImmutableCollection<OWLAxiom> provideOntologyAxioms() {
        try {
            var ontologyManager = OWLManager.createOWLOntologyManager();
            var ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);
            return ImmutableSet.copyOf(ontology.getAxioms());
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException("Failed to load ontology: " + e.getMessage(), e);
        }
    }

    @Provides
    @TranslationSessionScope
    public OntologyDocumentId provideOntologyDocumentId() {
        return ontologyDocumentId
            .map(OntologyDocumentId::create)
            .orElseGet(OntologyDocumentId::create);
    }
}
