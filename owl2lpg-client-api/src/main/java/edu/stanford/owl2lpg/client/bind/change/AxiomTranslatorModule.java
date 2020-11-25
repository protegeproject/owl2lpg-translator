package edu.stanford.owl2lpg.client.bind.change;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.model.AugmentedEdgeInclusionCheckerModule;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.internal.IdProviderModule;
import edu.stanford.owl2lpg.translator.shared.BuiltInPrefixDeclarationsModule;
import edu.stanford.owl2lpg.translator.visitors.AnnotationObjectVisitor;
import edu.stanford.owl2lpg.translator.visitors.AnnotationSubjectVisitor;
import edu.stanford.owl2lpg.translator.visitors.AnnotationValueVisitor;
import edu.stanford.owl2lpg.translator.visitors.AxiomVisitor;
import edu.stanford.owl2lpg.translator.visitors.ClassExpressionVisitor;
import edu.stanford.owl2lpg.translator.visitors.DataVisitor;
import edu.stanford.owl2lpg.translator.visitors.EntityVisitor;
import edu.stanford.owl2lpg.translator.visitors.IndividualVisitor;
import edu.stanford.owl2lpg.translator.visitors.OntologyVisitor;
import edu.stanford.owl2lpg.translator.visitors.PropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataVisitorEx;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    IdProviderModule.class,
    AugmentedEdgeInclusionCheckerModule.class,
    BuiltInPrefixDeclarationsModule.class})
public abstract class AxiomTranslatorModule {

  @Binds
  @ProjectSingleton
  public abstract OWLAnnotationObjectVisitorEx<Translation>
  provideAnnotationObjectVisitor(AnnotationObjectVisitor impl);

  @Binds
  @ProjectSingleton
  public abstract OWLAnnotationSubjectVisitorEx<Translation>
  provideAnnotationSubjectVisitor(AnnotationSubjectVisitor impl);

  @Binds
  @ProjectSingleton
  public abstract OWLAnnotationValueVisitorEx<Translation>
  provideAnnotationValueVisitor(AnnotationValueVisitor impl);

  @Binds
  @ProjectSingleton
  public abstract OWLAxiomVisitorEx<Translation>
  provideAxiomVisitor(AxiomVisitor impl);

  @Binds
  @ProjectSingleton
  public abstract OWLClassExpressionVisitorEx<Translation>
  provideClassExpressionVisitor(ClassExpressionVisitor impl);

  @Binds
  @ProjectSingleton
  public abstract OWLPropertyExpressionVisitorEx<Translation>
  providePropertyExpressionVisitor(PropertyExpressionVisitor impl);

  @Binds
  @ProjectSingleton
  public abstract OWLDataVisitorEx<Translation>
  provideDataVisitor(DataVisitor impl);

  @Binds
  @ProjectSingleton
  public abstract OWLEntityVisitorEx<Translation>
  provideEntityVisitor(EntityVisitor impl);

  @Binds
  @ProjectSingleton
  public abstract OWLIndividualVisitorEx<Translation>
  provideIndividualVisitor(IndividualVisitor impl);

  @Binds
  @ProjectSingleton
  public abstract OWLNamedObjectVisitorEx<Translation>
  provideOntologyVisitor(OntologyVisitor impl);
}
