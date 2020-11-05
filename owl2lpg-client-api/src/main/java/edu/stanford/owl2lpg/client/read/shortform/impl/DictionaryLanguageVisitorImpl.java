package edu.stanford.owl2lpg.client.read.shortform.impl;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.shortform.AnnotationAssertionDictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguageVisitor;
import edu.stanford.bmir.protege.web.shared.shortform.LocalNameDictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.shortform.OboIdDictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.shortform.PrefixedNameDictionaryLanguage;
import edu.stanford.owl2lpg.client.read.Parameters;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
class DictionaryLanguageVisitorImpl implements DictionaryLanguageVisitor<Optional<String>> {

  private static final String SHORT_FORMS_BY_ANNOTATION_ASSERTION_QUERY_FILE = "read/shortforms/short-form-by-annotation-assertion.cpy";
  private static final String SHORT_FORMS_BY_PREFIXED_NAME_QUERY_FILE = "read/shortforms/short-form-by-prefixed-name.cpy";
  private static final String SHORT_FORMS_BY_LOCAL_NAME_QUERY_FILE = "read/shortforms/short-form-by-local-name.cpy";
  private static final String SHORT_FORMS_BY_OBO_ID_QUERY_FILE = "read/shortforms/short-form-by-obo-id.cpy";

  private static final String SHORT_FORMS_BY_ANNOTATION_ASSERTION_QUERY = read(SHORT_FORMS_BY_ANNOTATION_ASSERTION_QUERY_FILE);
  private static final String SHORT_FORMS_BY_PREFIXED_NAME_QUERY = read(SHORT_FORMS_BY_PREFIXED_NAME_QUERY_FILE);
  private static final String SHORT_FORMS_BY_LOCAL_NAME_QUERY = read(SHORT_FORMS_BY_LOCAL_NAME_QUERY_FILE);
  private static final String SHORT_FORMS_BY_OBO_ID_QUERY = read(SHORT_FORMS_BY_OBO_ID_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final OWLEntity entity;

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  DictionaryLanguageVisitorImpl(@Nonnull Driver driver,
                                @Nonnull OWLEntity entity,
                                @Nonnull ProjectId projectId,
                                @Nonnull BranchId branchId) {
    this.driver = checkNotNull(driver);
    this.entity = checkNotNull(entity);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
  }

  @Override
  public Optional<String> visit(@Nonnull AnnotationAssertionDictionaryLanguage dictLang) {
    var inputParams = Parameters.forAnnotationAssertion(entity.getIRI(),
        dictLang.getAnnotationPropertyIri(),
        dictLang.getLang(),
        projectId, branchId);
    return getShortForm(SHORT_FORMS_BY_ANNOTATION_ASSERTION_QUERY, inputParams);
  }

  @Override
  public Optional<String> visit(@Nonnull PrefixedNameDictionaryLanguage dictLang) {
    var inputParams = Parameters.forEntity(entity, projectId, branchId);
    return getShortForm(SHORT_FORMS_BY_PREFIXED_NAME_QUERY, inputParams);
  }

  @Override
  public Optional<String> visit(@Nonnull LocalNameDictionaryLanguage dictLang) {
    var inputParams = Parameters.forEntity(entity, projectId, branchId);
    return getShortForm(SHORT_FORMS_BY_LOCAL_NAME_QUERY, inputParams);
  }

  @Override
  public Optional<String> visit(@Nonnull OboIdDictionaryLanguage dictLang) {
    var inputParams = Parameters.forEntity(entity, projectId, branchId);
    return getShortForm(SHORT_FORMS_BY_OBO_ID_QUERY, inputParams);
  }

  @Nonnull
  private Optional<String> getShortForm(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var result = tx.run(queryString, inputParams);
        var output = "";
        if (result.hasNext()) {
          output = result.single().get("shortForm").asString();
        }
        return (!isNullOrEmpty(output)) ? Optional.of(output) : Optional.empty();
      });
    }
  }
}