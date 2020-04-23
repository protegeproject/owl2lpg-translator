package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.translator.Translation;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A collection of factory methods to create OWL object visitors
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class VisitorFactory {

  private final NodeIdMapper nodeIdMapper;

  public VisitorFactory(@Nonnull NodeIdMapper nodeIdMapper) {
    this.nodeIdMapper = checkNotNull(nodeIdMapper);
  }

  @Nonnull
  public OWLEntityVisitorEx<Translation> createEntityVisitor() {
    return new EntityVisitor(nodeIdMapper, this);
  }

  @Nonnull
  public OWLDataVisitorEx<Translation> createDataVisitor() {
    return new DataVisitor(nodeIdMapper, this);
  }

  @Nonnull
  public OWLIndividualVisitorEx<Translation> createIndividualVisitor() {
    return new IndividualVisitor(nodeIdMapper, this);
  }

  @Nonnull
  public OWLAnnotationSubjectVisitorEx<Translation> createAnnotationSubjectVisitor() {
    return new AnnotationSubjectVisitor(nodeIdMapper, this);
  }

  public OWLAnnotationObjectVisitorEx<Translation> createAnnotationObjectVisitor() {
    return new AnnotationObjectVisitor(nodeIdMapper, this);
  }

  @Nonnull
  public OWLAnnotationValueVisitorEx<Translation> createAnnotationValueVisitor() {
    return new AnnotationValueVisitor(nodeIdMapper, this);
  }

  @Nonnull
  public OWLClassExpressionVisitorEx<Translation> createClassExpressionVisitor() {
    return new ClassExpressionVisitor(nodeIdMapper, this);
  }

  @Nonnull
  public OWLPropertyExpressionVisitorEx<Translation> createPropertyExpressionVisitor() {
    return new PropertyExpressionVisitor(nodeIdMapper, this);
  }

  @Nonnull
  public OWLAxiomVisitorEx<Translation> createAxiomVisitor() {
    return new AxiomVisitor(nodeIdMapper, this);
  }

  @Nonnull
  public OWLNamedObjectVisitorEx<Translation> createOntologyVisitor() {
    return new OntologyVisitor(nodeIdMapper, this);
  }
}
