package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class GraphObject {

  @Nonnull
  @Id
  @GeneratedValue
  private Long id;

  public Long getId() {
    return id;
  }
}
