package edu.stanford.owl2lpg.exporter.csv;

import dagger.Component;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = OntologyModule.class)
@TranslationSessionScope
public interface OntologyCsvExporterComponent {

    OntologyCsvExporter getOntologyCsvExporter();
}
