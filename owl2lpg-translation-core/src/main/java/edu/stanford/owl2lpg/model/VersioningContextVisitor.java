package edu.stanford.owl2lpg.model;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface VersioningContextVisitor<O> {

  O visit(VersioningContext versioningContext);
}
