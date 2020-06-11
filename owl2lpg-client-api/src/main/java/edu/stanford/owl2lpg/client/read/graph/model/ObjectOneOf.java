package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.collect.ImmutableSet;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectOneOf;

import javax.annotation.Nullable;
import java.util.Objects;
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

  @Nullable
  public ImmutableSet<Individual> getIndividuals() {
    if (Objects.isNull(individuals)) {
      return null;
    } else {
      return ImmutableSet.copyOf(individuals);
    }
  }

  @Override
  public OWLObjectOneOf toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return dataFactory.getOWLObjectOneOf(
          individuals.stream()
              .map(individual -> individual.toOwlObject(dataFactory, session))
              .collect(Collectors.toSet()));
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 1);
      return object.toOwlObject(dataFactory, session);
    }
  }
}
