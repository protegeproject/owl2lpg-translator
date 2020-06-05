package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.collect.ImmutableSet;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;

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

  public ImmutableSet<ClassExpression> getClassExpressions() {
    return ImmutableSet.copyOf(classExpressions);
  }

  @Override
  public OWLObjectIntersectionOf toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLObjectIntersectionOf(
        classExpressions.stream()
            .map(ce -> ce.toOwlObject(dataFactory))
            .collect(Collectors.toSet()));
  }
}
