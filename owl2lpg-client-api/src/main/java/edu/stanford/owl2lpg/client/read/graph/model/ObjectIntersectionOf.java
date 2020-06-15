package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.collect.ImmutableSet;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectIntersectionOf")
public class ObjectIntersectionOf extends ClassExpression<OWLObjectIntersectionOf> {

  @Relationship(type = "CLASS_EXPRESSION")
  private Set<ClassExpression> classExpressions;

  private ObjectIntersectionOf() {
  }

  @Nullable
  public ImmutableSet<ClassExpression> getClassExpressions() {
    if (Objects.isNull(classExpressions)) {
      return null;
    } else {
      return ImmutableSet.copyOf(classExpressions);
    }
  }

  @Override
  public OWLObjectIntersectionOf toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (classExpressions == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return dataFactory.getOWLObjectIntersectionOf(
          classExpressions.stream()
              .map(ce -> ce.toOwlObject(dataFactory, session))
              .collect(Collectors.toSet()));
    }
  }

  private ObjectIntersectionOf reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
  }
}
