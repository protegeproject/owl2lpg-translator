package edu.stanford.owl2lpg.translator.utils;

import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.Properties;

/**
 * A utility code for creating the node and edge properties simple and clean
 * by importing the methods statically.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertiesFactory {

  /**
   * Creates a Properties instance with a single key-value pair.
   *
   * @param key   The field name
   * @param value The field value
   * @return An instance of java.util.Properties
   */
  public static Properties Properties(String key, Object value) {
    return Properties.create(ImmutableMap.of(key, value));
  }
}
