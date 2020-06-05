package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.collect.ImmutableSet;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectUnionOf")
public class ObjectUnionOf extends ClassExpression<OWLObjectUnionOf> {

  @Relationship(type = "CLASS_EXPRESSION")
  private Set<ClassExpression> unionClasses;

  private ObjectUnionOf() {
  }

  public ImmutableSet<ClassExpression> getClassExpressions() {
    return ImmutableSet.copyOf(unionClasses);
  }

  @Override
  public OWLObjectUnionOf toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLObjectUnionOf(
        unionClasses.stream()
            .map(ope -> ope.toOwlObject(dataFactory))
            .collect(Collectors.toSet()));
  }
}
