package edu.stanford.owl2lpg.client.read.frame2;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormIndex;
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
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.read.axiom.CypherQueries.SHORT_FORMS_INDEX_QUERY;
import static org.semanticweb.owlapi.model.EntityType.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CypherMultiLingualShortFormIndex implements MultiLingualShortFormIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final Session session;

  private final OWLDataFactory dataFactory = new OWLDataFactoryImpl();

  @Inject
  public CypherMultiLingualShortFormIndex(@Nonnull ProjectId projectId,
                                          @Nonnull BranchId branchId,
                                          @Nonnull Session session) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.session = checkNotNull(session);
  }

  @Nonnull
  @Override
  public Stream<OWLEntity> getEntities(@Nonnull String entityName,
                                       @Nonnull List<DictionaryLanguage> languages) {
    var entityMap = getEntities(entityName);
    return languages
        .stream()
        .map(language -> entityMap.getOrDefault(language, null))
        .filter(Objects::nonNull);
  }

  @Nonnull
  public ImmutableMap<DictionaryLanguage, OWLEntity> getEntities(@Nonnull String entityName) {
    var args = Parameters.forShortFormsIndex(projectId, branchId, entityName);
    return session.readTransaction(tx -> {
      var mutableDictionaryMap = Maps.<DictionaryLanguage, OWLEntity>newHashMap();
      var result = tx.run(SHORT_FORMS_INDEX_QUERY, args);
      while (result.hasNext()) {
        var row = result.next().asMap();
        var entityNode = (Node) row.get("entity");
        var propertyNode = (Node) row.get("annotationProperty");
        var literalNode = (Node) row.get("value");
        var entity = getOwlEntity(entityNode);
        var dictionaryLanguage = getDictionaryLanguage(propertyNode, literalNode);
        mutableDictionaryMap.put(dictionaryLanguage, entity);
      }
      return ImmutableMap.copyOf(mutableDictionaryMap);
    });
  }

  @Nonnull
  private OWLEntity getOwlEntity(Node entityNode) {
    var entityIri = IRI.create(entityNode.get(PropertyFields.IRI).asString());
    var entityType = entityTypeMap.get(entityNode.labels());
    return dataFactory.getOWLEntity(entityType, entityIri);
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
