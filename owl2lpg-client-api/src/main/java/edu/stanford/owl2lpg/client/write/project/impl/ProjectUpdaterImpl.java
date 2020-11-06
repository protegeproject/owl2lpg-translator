package edu.stanford.owl2lpg.client.write.project.impl;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.write.project.ProjectUpdater;
import org.neo4j.driver.Driver;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProjectUpdaterImpl implements ProjectUpdater {

  private static final String SET_DEFAULT_BRANCH_ID_QUERY_FILE = "read/ontology/set-default-branch-id.cpy";
  private static final String SET_DEFAULT_ONTOLOGY_DOCUMENT_ID_QUERY_FILE = "read/ontology/set-default-ontology-document-id.cpy";

  private static final String SET_DEFAULT_BRANCH_ID_QUERY = read(SET_DEFAULT_BRANCH_ID_QUERY_FILE);
  private static final String SET_DEFAULT_ONTOLOGY_DOCUMENT_ID_QUERY = read(SET_DEFAULT_ONTOLOGY_DOCUMENT_ID_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  public ProjectUpdaterImpl(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  @Override
  public boolean setDefaultBranchId(@Nonnull ProjectId projectId, @Nonnull BranchId defaultBranchId) {
    var inputParams = Parameters.forContext(projectId, defaultBranchId);
    try (var session = driver.session()) {
      return session.writeTransaction(tx -> {
        var result = tx.run(SET_DEFAULT_BRANCH_ID_QUERY, inputParams);
        var counters = result.consume().counters();
        return counters.propertiesSet() > 0;
      });
    }
  }

  @Override
  public boolean setDefaultOntologyDocumentId(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull OntologyDocumentId defaultOntDocId) {
    var inputParams = Parameters.forContext(projectId, branchId, defaultOntDocId);
    try (var session = driver.session()) {
      return session.writeTransaction(tx -> {
        var result = tx.run(SET_DEFAULT_ONTOLOGY_DOCUMENT_ID_QUERY, inputParams);
        var counters = result.consume().counters();
        return counters.propertiesSet() > 0;
      });
    }
  }
}
