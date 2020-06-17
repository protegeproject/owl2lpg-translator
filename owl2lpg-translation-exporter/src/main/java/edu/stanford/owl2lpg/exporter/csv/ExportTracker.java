package edu.stanford.owl2lpg.exporter.csv;

import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ExportTracker<T> {

  boolean contains(T obj);

  void add(T obj, Consumer<T> callback);

  int size();
}
