package edu.stanford.owl2lpg.client.read.entity.impl;

import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

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
    var iri = IRI.create(node.get(PropertyFields.IRI).asString());
    var entity = Optional.<OWLEntity>empty();
    if (nodeLabels.contains(NodeLabels.CLASS.getMainLabel())) {
      entity = Optional.of(dataFactory.getOWLClass(iri));
    } else if (nodeLabels.contains(NodeLabels.OBJECT_PROPERTY.getMainLabel())) {
      entity = Optional.of(dataFactory.getOWLObjectProperty(iri));
    } else if (nodeLabels.contains(NodeLabels.DATA_PROPERTY.getMainLabel())) {
      entity = Optional.of(dataFactory.getOWLDataProperty(iri));
    } else if (nodeLabels.contains(NodeLabels.ANNOTATION_PROPERTY.getMainLabel())) {
      entity = Optional.of(dataFactory.getOWLAnnotationProperty(iri));
    } else if (nodeLabels.contains(NodeLabels.DATATYPE.getMainLabel())) {
      entity = Optional.of(dataFactory.getOWLDatatype(iri));
    } else if (nodeLabels.contains(NodeLabels.NAMED_INDIVIDUAL.getMainLabel())) {
      entity = Optional.of(dataFactory.getOWLNamedIndividual(iri));
    }
    return entity.orElseThrow(() -> new RuntimeException("Node " + node + " is not an OWL entity"));
  }
}
