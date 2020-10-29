package edu.stanford.owl2lpg.client.read.entity.impl;

import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityNodeMapper {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public EntityNodeMapper(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Nonnull
  public OWLEntity toOwlEntity(Node node) {
    var nodeLabels = Lists.newArrayList(node.labels());
    if (nodeLabels.contains(NodeLabels.CLASS.getMainLabel())) {
      return toOwlClass(node);
    } else if (nodeLabels.contains(NodeLabels.OBJECT_PROPERTY.getMainLabel())) {
      return toOwlObjectProperty(node);
    } else if (nodeLabels.contains(NodeLabels.DATA_PROPERTY.getMainLabel())) {
      return toOwlDataProperty(node);
    } else if (nodeLabels.contains(NodeLabels.ANNOTATION_PROPERTY.getMainLabel())) {
      return toOwlAnnotationProperty(node);
    } else if (nodeLabels.contains(NodeLabels.DATATYPE.getMainLabel())) {
      return toOwlDatatype(node);
    } else if (nodeLabels.contains(NodeLabels.NAMED_INDIVIDUAL.getMainLabel())) {
      return toOwlNamedIndividual(node);
    } else {
      throw new RuntimeException("Node " + node + " is not an OWL entity node");
    }
  }

  @Nonnull
  public OWLClass toOwlClass(Node node) {
    var iriString = getIriString(node);
    return dataFactory.getOWLClass(IRI.create(iriString));
  }

  @Nonnull
  public OWLObjectProperty toOwlObjectProperty(Node node) {
    var iriString = getIriString(node);
    return dataFactory.getOWLObjectProperty(IRI.create(iriString));
  }

  @Nonnull
  public OWLDataProperty toOwlDataProperty(Node node) {
    var iriString = getIriString(node);
    return dataFactory.getOWLDataProperty(IRI.create(iriString));
  }

  @Nonnull
  public OWLAnnotationProperty toOwlAnnotationProperty(Node node) {
    var iriString = getIriString(node);
    return dataFactory.getOWLAnnotationProperty(IRI.create(iriString));
  }

  @Nonnull
  public OWLDatatype toOwlDatatype(Node node) {
    var iriString = getIriString(node);
    return dataFactory.getOWLDatatype(IRI.create(iriString));
  }

  @Nonnull
  public OWLNamedIndividual toOwlNamedIndividual(Node node) {
    var iriString = getIriString(node);
    return dataFactory.getOWLNamedIndividual(IRI.create(iriString));
  }

  @Nonnull
  private String getIriString(Node node) {
    return node.get(PropertyFields.IRI).asString();
  }
}
