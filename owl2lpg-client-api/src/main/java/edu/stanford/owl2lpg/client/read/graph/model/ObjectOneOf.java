package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.collect.ImmutableSet;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectOneOf;

import javax.annotation.Nullable;
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
    return individuals != null ? ImmutableSet.copyOf(individuals) : null;
  }

  @Override
  public OWLObjectOneOf toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (individuals == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return dataFactory.getOWLObjectOneOf(
          individuals.stream()
              .map(individual -> individual.toOwlObject(dataFactory, session))
              .collect(Collectors.toSet()));
    }
  }

  private ObjectOneOf reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
  }
}
