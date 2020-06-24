package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomQueries {

  private static final String AXIOM_SUBJECT_QUERY_FILE = "axioms/axiom-subject.cpy";

  public static final String AXIOM_SUBJECT_QUERY = readResource(AXIOM_SUBJECT_QUERY_FILE);

  private static String readResource(String fileName) {
    try {
      return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
