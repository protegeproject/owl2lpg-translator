package edu.stanford.owl2lpg.client.write.handlers;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.model.AugmentedEdgeInclusionChecker;
import edu.stanford.owl2lpg.model.EdgeIdProvider;
import edu.stanford.owl2lpg.model.IdFormatChecker;
import edu.stanford.owl2lpg.model.NodeIdMapper;
import edu.stanford.owl2lpg.model.NodeIdMapperImpl;
import edu.stanford.owl2lpg.model.NodeIdProvider;
import edu.stanford.owl2lpg.model.SingleEncounterNodeChecker;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.internal.AugmentedEdgeInclusionCheckerImpl;
import edu.stanford.owl2lpg.translator.internal.DigestEdgeIdProvider;
import edu.stanford.owl2lpg.translator.internal.DigestNodeIdProvider;
import edu.stanford.owl2lpg.translator.internal.IdFormatCheckerImpl;
import edu.stanford.owl2lpg.translator.internal.NumberIncrementIdProvider;
import edu.stanford.owl2lpg.translator.internal.SingleEncounterNodeCheckerImpl;
import edu.stanford.owl2lpg.translator.shared.BuiltInPrefixDeclarationsModule;
import edu.stanford.owl2lpg.translator.shared.DigestFunctionModule;
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

import javax.inject.Named;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    DigestFunctionModule.class,
    BuiltInPrefixDeclarationsModule.class})
public abstract class OntologyObjectTranslatorModule {

  @Binds
  @ProjectSingleton
  public abstract NodeIdMapper provideNodeIdMapper(NodeIdMapperImpl impl);

  @Binds
  @Named("number")
  @ProjectSingleton
  public abstract NodeIdProvider
  provideNodeIdProvider(NumberIncrementIdProvider impl);

  @Binds
  @Named("digest")
  @ProjectSingleton
  public abstract NodeIdProvider
  provideDigestNodeIdProvider(DigestNodeIdProvider impl);

  @Binds
  public abstract IdFormatChecker
  provideIdFormatChecker(IdFormatCheckerImpl impl);

  @Binds
  public abstract SingleEncounterNodeChecker
  provideNodeObjectCheckerForSingleEncounter(SingleEncounterNodeCheckerImpl impl);

  @Binds
  @ProjectSingleton
  public abstract EdgeIdProvider
  provideEdgeIdProvider(DigestEdgeIdProvider impl);

  @Binds
  public abstract AugmentedEdgeInclusionChecker
  provideAugmentedEdgeInclusionChecker(AugmentedEdgeInclusionCheckerImpl impl);

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
