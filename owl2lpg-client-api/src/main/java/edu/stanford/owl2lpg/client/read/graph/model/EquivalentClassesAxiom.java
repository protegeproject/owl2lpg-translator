package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.collect.ImmutableSet;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

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

  public EquivalentClassesAxiom(@Nonnull Set<ClassExpression> classExpressions) {
    this.classExpressions = checkNotNull(classExpressions);
  }

  @Nullable
  public ImmutableSet<ClassExpression> getEquivalentClasses() {
    return ImmutableSet.copyOf(classExpressions);
  }

  public OWLEquivalentClassesAxiom toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (classExpressions == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return dataFactory.getOWLEquivalentClassesAxiom(
          classExpressions.stream()
              .map(ce -> ce.toOwlObject(dataFactory, session))
              .collect(Collectors.toSet()));
    }
  }

  private EquivalentClassesAxiom reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
  }
}
