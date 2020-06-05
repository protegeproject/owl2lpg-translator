package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.collect.ImmutableSet;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "EquivalentClasses")
public class EquivalentClassesAxiom extends ClassAxiom<OWLEquivalentClassesAxiom> {

  @Relationship(type = "CLASS_EXPRESSION")
  private Set<ClassExpression> classExpressions;

  private EquivalentClassesAxiom() {
  }

  public ImmutableSet<ClassExpression> getEquivalentClasses() {
    return ImmutableSet.copyOf(classExpressions);
  }

  public OWLEquivalentClassesAxiom toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLEquivalentClassesAxiom(
        classExpressions.stream()
            .map(ce -> ce.toOwlObject(dataFactory))
            .collect(Collectors.toSet()));
  }
}
