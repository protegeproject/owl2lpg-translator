package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.index.IndividualsQueryResult;
import edu.stanford.bmir.protege.web.server.lang.LanguageManager;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualDictionary;
import edu.stanford.bmir.protege.web.server.shortform.Scanner;
import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.server.util.Counter;
import edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.hierarchy.ClassHierarchyAccessor;
import edu.stanford.owl2lpg.client.read.individual.NamedIndividualAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.server.pagination.PageCollector.toPage;
import static edu.stanford.bmir.protege.web.shared.DataFactory.getOWLThing;
import static edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode.ALL_INSTANCES;
import static edu.stanford.bmir.protege.web.shared.individuals.InstanceRetrievalMode.DIRECT_INSTANCES;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jIndividualsByNameIndex {

  @Nonnull
  private final OWLClass root;

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final LanguageManager languageManager;

  @Nonnull
  private final NamedIndividualAccessor namedIndividualAccessor;

  @Nonnull
  private final ClassHierarchyAccessor classHierarchyAccessor;

  @Nonnull
  private final MultiLingualDictionary multiLingualDictionary;

  @Inject
  public Neo4jIndividualsByNameIndex(@Nonnull OWLClass root,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull DocumentIdMap documentIdMap,
                                     @Nonnull LanguageManager languageManager,
                                     @Nonnull NamedIndividualAccessor namedIndividualAccessor,
                                     @Nonnull ClassHierarchyAccessor classHierarchyAccessor,
                                     @Nonnull MultiLingualDictionary multiLingualDictionary) {
    this.root = checkNotNull(root);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.languageManager = checkNotNull(languageManager);
    this.namedIndividualAccessor = checkNotNull(namedIndividualAccessor);
    this.classHierarchyAccessor = checkNotNull(classHierarchyAccessor);
    this.multiLingualDictionary = checkNotNull(multiLingualDictionary);
  }

  @Nonnull
  public IndividualsQueryResult getIndividuals(@Nonnull String searchString,
                                               @Nonnull PageRequest pageRequest) {
    return getIndividuals(getOWLThing(), ALL_INSTANCES, searchString, pageRequest);
  }

  @Nonnull
  public IndividualsQueryResult getIndividuals(@Nonnull OWLClass owlClass,
                                               @Nonnull InstanceRetrievalMode instanceRetrievalMode,
                                               @Nonnull String searchString,
                                               @Nonnull PageRequest pageRequest) {
    var searchStrings = SearchString.parseMultiWordSearchString(searchString);
    switch (instanceRetrievalMode) {
      case ALL_INSTANCES:
        return getAllInstances(owlClass, searchStrings, pageRequest);
      case DIRECT_INSTANCES:
        return getDirectInstances(owlClass, searchStrings, pageRequest);
      default:
        return IndividualsQueryResult.get(Page.emptyPage(), 0, owlClass, ALL_INSTANCES);
    }
  }

  @Nonnull
  private IndividualsQueryResult getAllInstances(OWLClass owlClass, List<SearchString> searchStrings, PageRequest pageRequest) {
    var counter = new Counter();
    var page = Optional.<Page<OWLNamedIndividual>>empty();
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      page = getAllInstances(searchStrings)
          .peek(individual -> counter.increment())
          .collect(toPage(pageRequest.getPageNumber(), pageRequest.getPageSize()));
    } else {
      page = Streams.concat(getDirectInstances(owlClass, searchStrings), getIndirectInstances(owlClass, searchStrings))
          .peek(individual -> counter.increment())
          .collect(toPage(pageRequest.getPageNumber(), pageRequest.getPageSize()));
    }
    return IndividualsQueryResult.get(page.orElse(Page.emptyPage()),
        counter.getCounter(),
        owlClass,
        ALL_INSTANCES);
  }

  @Nonnull
  private Stream<OWLNamedIndividual> getAllInstances(List<SearchString> searchStrings) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> namedIndividualAccessor.getAllIndividuals(projectId, branchId, documentId).stream())
        .filter(individual -> matchesSearchStrings(individual, searchStrings));
  }

  @Nonnull
  private IndividualsQueryResult getDirectInstances(OWLClass owlClass, List<SearchString> searchStrings, PageRequest pageRequest) {
    var counter = new Counter();
    var page = getDirectInstances(owlClass, searchStrings)
        .peek(individual -> counter.increment())
        .collect(toPage(pageRequest.getPageNumber(), pageRequest.getPageSize()));
    return IndividualsQueryResult.get(page.orElse(Page.emptyPage()),
        counter.getCounter(),
        owlClass,
        DIRECT_INSTANCES);
  }

  @Nonnull
  private Stream<OWLNamedIndividual> getDirectInstances(OWLClass owlClass, List<SearchString> searchStrings) {
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return getAllInstances(searchStrings);
    } else {
      return documentIdMap.get(projectId)
          .stream()
          .flatMap(documentId -> namedIndividualAccessor.getIndividualsByType(owlClass, projectId, branchId, documentId).stream())
          .filter(individual -> matchesSearchStrings(individual, searchStrings));
    }
  }

  @Nonnull
  private Stream<OWLNamedIndividual> getIndirectInstances(OWLClass owlClass, List<SearchString> searchStrings) {
    return documentIdMap.get(projectId)
        .stream()
        .flatMap(documentId -> classHierarchyAccessor.getDescendants(owlClass, projectId, branchId, documentId).stream())
        .flatMap(cls -> getDirectInstances(cls, searchStrings))
        .distinct();
  }

  private boolean matchesSearchStrings(@Nonnull OWLNamedIndividual individual,
                                       @Nonnull List<SearchString> searchStrings) {
    if (searchStrings.isEmpty()) {
      return true;
    }
    var shortForm = multiLingualDictionary.getShortForm(individual, languageManager.getLanguages(), "");
    Scanner scanner = new Scanner(shortForm, shortForm.toLowerCase());
    for (SearchString searchString : searchStrings) {
      int index = scanner.indexOf(searchString, 0);
      if (index == -1) {
        return false;
      }
    }
    return true;
  }
}
