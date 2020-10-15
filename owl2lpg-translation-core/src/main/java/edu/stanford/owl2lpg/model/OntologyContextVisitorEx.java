package edu.stanford.owl2lpg.model;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface OntologyContextVisitorEx<O> {

  O visit(OntologyContext ontologyContext);
}
