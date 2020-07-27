package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.semanticweb.owlapi.model.EntityType.*;

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
    var entityType = getEntityTypeFromNodeLabels(entityNode.labels());
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

  private static EntityType<?> getEntityTypeFromNodeLabels(Iterable<String> nodeLabels) {
    var labelList = Lists.newArrayList(nodeLabels);
    if (isEqual(labelList, NodeLabels.CLASS.asList())) {
      return CLASS;
    } else if (isEqual(labelList, NodeLabels.OBJECT_PROPERTY.asList())) {
      return OBJECT_PROPERTY;
    } else if (isEqual(labelList, NodeLabels.DATA_PROPERTY.asList())) {
      return DATA_PROPERTY;
    } else if (isEqual(labelList, NodeLabels.ANNOTATION_PROPERTY.asList())) {
      return ANNOTATION_PROPERTY;
    } else if (isEqual(labelList, NodeLabels.NAMED_INDIVIDUAL.asList())) {
      return NAMED_INDIVIDUAL;
    } else if (isEqual(labelList, NodeLabels.DATATYPE.asList())) {
      return DATATYPE;
    }
    throw new NullPointerException("Cannot find an entity type for labels: " + labelList);
  }

  private static boolean isEqual(List<String> list1, List<String> list2) {
    return Sets.newHashSet(list1).equals(Sets.newHashSet(list2));
  }
}
