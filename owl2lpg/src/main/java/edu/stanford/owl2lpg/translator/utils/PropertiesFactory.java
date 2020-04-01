package edu.stanford.owl2lpg.translator.utils;

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
    return PropertiesBuilder.create().set(key, value).build();
  }

  /**
   * Creates a Properties instance with multiple key-value pairs.
   * The value's order in the {@param keys} and {@param values}
   * arrays must be properly matched.
   *
   * @param keys   The field names
   * @param values The field values
   * @return An instance of java.util.Properties
   */
  public static Properties Properties(String[] keys, Object[] values) {
    PropertiesBuilder builder = PropertiesBuilder.create();
    for (int i = 0; i < keys.length; i++) {
      builder = builder.set(keys[i], values[i]);
    }
    return builder.build();
  }
}
