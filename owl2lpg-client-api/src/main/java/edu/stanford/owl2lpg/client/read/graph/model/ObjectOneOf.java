package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.collect.ImmutableSet;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectOneOf;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectOneOf")
public class ObjectOneOf extends ClassExpression<OWLObjectOneOf> {

  @Relationship(type = "INDIVIDUAL")
  private Set<Individual> individuals;

  private ObjectOneOf() {
  }

  public ImmutableSet<Individual> getIndividuals() {
    return ImmutableSet.copyOf(individuals);
  }

  @Override
  public OWLObjectOneOf toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLObjectOneOf(
        individuals.stream()
            .map(individual -> individual.toOwlObject(dataFactory))
            .collect(Collectors.toSet()));
  }
}
