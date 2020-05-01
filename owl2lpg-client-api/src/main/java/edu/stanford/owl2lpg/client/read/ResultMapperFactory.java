package edu.stanford.owl2lpg.client.read;

import org.neo4j.driver.Result;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ResultMapperFactory {

  public ResultClassFrame getResultClassFrame(Result result) {
    return new ResultClassFrame(result);
  }
}
