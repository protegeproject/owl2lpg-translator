package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "SubClassOf")
public class SubClassOfAxiom extends ClassAxiom<OWLSubClassOfAxiom> {

  @Relationship(type = "SUB_CLASS_EXPRESSION")
  private ClassExpression subClass;

  @Relationship(type = "SUPER_CLASS_EXPRESSION")
  private ClassExpression superClass;

  private SubClassOfAxiom() {
  }

  public SubClassOfAxiom(@Nonnull ClassExpression subClass,
                         @Nonnull ClassExpression superClass) {
    this.subClass = checkNotNull(subClass);
    this.superClass = checkNotNull(superClass);
  }

  @Nullable
  public ClassExpression getSubClass() {
    return subClass;
  }

  @Nullable
  public ClassExpression getSuperClass() {
    return superClass;
  }

  @Override
  public OWLSubClassOfAxiom toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (subClass == null || superClass == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return dataFactory.getOWLSubClassOfAxiom(
          subClass.toOwlObject(dataFactory, session),
          superClass.toOwlObject(dataFactory, session));
    }
  }

  private SubClassOfAxiom reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
  }
}
