package edu.stanford.owl2lpg.translator;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.model.AugmentedEdgeFactoryModule;
import edu.stanford.owl2lpg.model.EdgeFactoryModule;
import edu.stanford.owl2lpg.model.Translation;
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
    EdgeFactoryModule.class,
    AugmentedEdgeFactoryModule.class,
    BuiltInPrefixDeclarationsModule.class
})
public abstract class TranslatorModule {

  @Binds
  @TranslationSessionScope
  public abstract OWLAnnotationObjectVisitorEx<Translation>
  provideAnnotationObjectVisitor(AnnotationObjectVisitor impl);

  @Binds
  @TranslationSessionScope
  public abstract OWLAnnotationSubjectVisitorEx<Translation>
  provideAnnotationSubjectVisitor(AnnotationSubjectVisitor impl);

  @Binds
  @TranslationSessionScope
  public abstract OWLAnnotationValueVisitorEx<Translation>
  provideAnnotationValueVisitor(AnnotationValueVisitor impl);

  @Binds
  @TranslationSessionScope
  public abstract OWLAxiomVisitorEx<Translation>
  provideAxiomVisitor(AxiomVisitor impl);

  @Binds
  @TranslationSessionScope
  public abstract OWLClassExpressionVisitorEx<Translation>
  provideClassExpressionVisitor(ClassExpressionVisitor impl);

  @Binds
  @TranslationSessionScope
  public abstract OWLPropertyExpressionVisitorEx<Translation>
  providePropertyExpressionVisitor(PropertyExpressionVisitor impl);

  @Binds
  @TranslationSessionScope
  public abstract OWLDataVisitorEx<Translation>
  provideDataVisitor(DataVisitor impl);

  @Binds
  @TranslationSessionScope
  public abstract OWLEntityVisitorEx<Translation>
  provideEntityVisitor(EntityVisitor impl);

  @Binds
  @TranslationSessionScope
  public abstract OWLIndividualVisitorEx<Translation>
  provideIndividualVisitor(IndividualVisitor impl);

  @Binds
  @TranslationSessionScope
  public abstract OWLNamedObjectVisitorEx<Translation>
  provideOntologyVisitor(OntologyVisitor impl);
}
