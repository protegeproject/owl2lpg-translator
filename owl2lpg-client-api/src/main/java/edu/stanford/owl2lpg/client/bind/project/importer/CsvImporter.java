package edu.stanford.owl2lpg.client.bind.project.importer;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface CsvImporter {

  boolean loadOntologyProject(@Nonnull String directoryName);
}
