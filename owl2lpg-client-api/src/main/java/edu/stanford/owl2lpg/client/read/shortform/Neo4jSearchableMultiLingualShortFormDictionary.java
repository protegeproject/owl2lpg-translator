package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.ImmutableIntArray;
import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.server.shortform.SearchableMultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.server.shortform.ShortFormMatch;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.frame.Parameters;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static org.semanticweb.owlapi.model.EntityType.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSearchableMultiLingualShortFormDictionary implements SearchableMultiLingualShortFormDictionary {

  private static final String SEARCHABLE_SHORT_FORMS_QUERY_FILE = "shortforms/searchable-short-forms-dictionary.cpy";
  private static final String SEARCHABLE_SHORT_FORMS_QUERY = read(SEARCHABLE_SHORT_FORMS_QUERY_FILE);

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final Session session;

  @Nonnull
  private final FullTextIndexName annotationValueFullTextIndexName;

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public Neo4jSearchableMultiLingualShortFormDictionary(@Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull Session session,
                                                        @Nonnull FullTextIndexName annotationValueFullTextIndexName,
                                                        @Nonnull OWLDataFactory dataFactory) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.session = checkNotNull(session);
    this.annotationValueFullTextIndexName = checkNotNull(annotationValueFullTextIndexName);
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Nonnull
  @Override
  public Stream<ShortFormMatch> getShortFormsContaining(@Nonnull List<SearchString> searchStrings,
                                                        @Nonnull Set<EntityType<?>> entityTypes,
                                                        @Nonnull List<DictionaryLanguage> languages) {
    var shortFormMatchMultiMap = getShortFormsContaining(searchStrings, entityTypes);
    return languages
        .stream()
        .flatMap(language -> shortFormMatchMultiMap.get(language).stream())
        .distinct();
  }

  @Nonnull
  public Multimap<DictionaryLanguage, ShortFormMatch> getShortFormsContaining(List<SearchString> searchStrings,
                                                                              Set<EntityType<?>> entityTypes) {
    var args = Parameters.forShortFormsContaining(
        projectId, branchId, annotationValueFullTextIndexName, searchStrings);
    return session.readTransaction(tx -> {
      var mutableDictionaryMap = HashMultimap.<DictionaryLanguage, ShortFormMatch>create();
      var result = tx.run(SEARCHABLE_SHORT_FORMS_QUERY, args);
      while (result.hasNext()) {
        var row = result.next().asMap();
        var entityNode = (Node) row.get("entity");
        var propertyNode = (Node) row.get("annotationProperty");
        var literalNode = (Node) row.get("value");
        var entity = getOwlEntity(entityNode);
        if (entityTypes.contains(entity.getEntityType())) {
          var shortForm = getShortForm(literalNode);
          var dictionaryLanguage = getDictionaryLanguage(propertyNode, literalNode);
          ShortFormMatch shortFormMatch = getShortFormMatch(entity, shortForm, searchStrings, dictionaryLanguage);
          mutableDictionaryMap.put(dictionaryLanguage, shortFormMatch);
        }
      }
      return ImmutableMultimap.copyOf(mutableDictionaryMap);
    });
  }

  @Nonnull
  private OWLEntity getOwlEntity(Node entityNode) {
    var entityIri = IRI.create(entityNode.get(PropertyFields.IRI).asString());
    var entityType = entityTypeMap.get(entityNode.labels());
    return dataFactory.getOWLEntity(entityType, entityIri);
  }

  @Nonnull
  private String getShortForm(Node literalNode) {
    return literalNode.get(PropertyFields.LEXICAL_FORM).asString();
  }

  @Nonnull
  private DictionaryLanguage getDictionaryLanguage(Node propertyNode, Node literalNode) {
    var propertyIri = getAnnotationPropertyIri(propertyNode);
    var language = getLanguage(literalNode);
    return DictionaryLanguage.create(propertyIri, language);
  }

  @Nonnull
  private IRI getAnnotationPropertyIri(Node propertyNode) {
    return IRI.create(propertyNode.get(PropertyFields.IRI).asString());
  }

  @Nonnull
  private String getLanguage(Node literalNode) {
    return literalNode.hasLabel(PropertyFields.LANGUAGE)
        ? literalNode.get(PropertyFields.LANGUAGE).asString()
        : "";
  }

  @Nonnull
  private ShortFormMatch getShortFormMatch(OWLEntity entity, String shortForm,
                                           List<SearchString> searchStrings,
                                           DictionaryLanguage dictionaryLanguage) {
    var matchPositions = new int[searchStrings.size()];
    var matchCount = 0;
    for (var i = 0; i < searchStrings.size(); i++) {
      var searchString = searchStrings.get(i).getSearchString();
      var matchIndex = shortForm.toLowerCase().indexOf(searchString);
      matchPositions[i] = matchIndex;
      if (matchIndex != -1) {
        matchCount++;
      }
    }
    return ShortFormMatch.get(entity, shortForm, dictionaryLanguage, matchCount,
        ImmutableIntArray.copyOf(matchPositions));
  }

  private static final Map<List<String>, EntityType<?>> entityTypeMap =
      new ImmutableMap.Builder<List<String>, EntityType<?>>()
          .put(NodeLabels.CLASS.asList(), CLASS)
          .put(NodeLabels.OBJECT_PROPERTY.asList(), OBJECT_PROPERTY)
          .put(NodeLabels.DATA_PROPERTY.asList(), DATA_PROPERTY)
          .put(NodeLabels.ANNOTATION_PROPERTY.asList(), ANNOTATION_PROPERTY)
          .put(NodeLabels.NAMED_INDIVIDUAL.asList(), NAMED_INDIVIDUAL)
          .put(NodeLabels.DATATYPE.asList(), DATATYPE)
          .build();
}
