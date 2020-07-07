package edu.stanford.owl2lpg.client.util;

import com.google.common.base.Charsets;

import java.io.IOException;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Resources {

  public static String read(String fileName) {
    try {
      return com.google.common.io.Resources.toString(com.google.common.io.Resources.getResource(fileName), Charsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
