package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface CsvExportChecker {

  boolean isExported(Node node);

  boolean isExported(Edge edge);
}
