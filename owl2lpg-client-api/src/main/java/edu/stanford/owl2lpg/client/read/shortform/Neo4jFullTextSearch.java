package edu.stanford.owl2lpg.client.read.shortform;

import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface Neo4jFullTextSearch {

  @Nonnull
  EntityShortFormMatchesDictionary getShortFormsContaining(List<SearchString> searchStrings, PageRequest pageRequest);
}
