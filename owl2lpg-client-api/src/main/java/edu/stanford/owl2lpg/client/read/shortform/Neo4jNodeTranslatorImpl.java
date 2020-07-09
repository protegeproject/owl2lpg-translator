package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.semanticweb.owlapi.model.EntityType.*;
import static org.semanticweb.owlapi.model.EntityType.DATATYPE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jNodeTranslatorImpl implements Neo4jNodeTranslator {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public Neo4jNodeTranslatorImpl(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Nonnull
  @Override
  public OWLEntity getOwlEntity(Node entityNode) {
    var entityIri = IRI.create(entityNode.get(PropertyFields.IRI).asString());
    var entityType = entityTypeMap.get(entityNode.labels());
    return dataFactory.getOWLEntity(entityType, entityIri);
  }

  @Nonnull
  @Override
  public String getShortForm(Node literalNode) {
    return literalNode.get(PropertyFields.LEXICAL_FORM).asString();
  }

  @Nonnull
  @Override
  public DictionaryLanguage getDictionaryLanguage(Node propertyNode, Node literalNode) {
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
