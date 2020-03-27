package edu.stanford.owl2lpg.translator.impl;

import javax.annotation.Nonnull;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A helper code for building node and edge properties.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertiesBuilder {

  private final Properties properties;

  private PropertiesBuilder(@Nonnull Properties properties) {
    this.properties = checkNotNull(properties);
  }

  public static PropertiesBuilder create() {
    return new PropertiesBuilder(new Properties());
  }

  public static PropertiesBuilder from(Properties properties) {
    Properties newProperties = new Properties();
    properties.forEach((key, value) -> {
      newProperties.setProperty((String) key, (String) value);
    });
    return new PropertiesBuilder(newProperties);
  }

  public PropertiesBuilder set(String key, Object value) {
    properties.setProperty(key, value.toString());
    return this;
  }

  public Properties build() {
    return properties;
  }
}
