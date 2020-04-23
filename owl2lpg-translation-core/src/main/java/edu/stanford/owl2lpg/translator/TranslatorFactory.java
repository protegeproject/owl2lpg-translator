package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.NodeIdMapper;
import edu.stanford.owl2lpg.translator.visitors.NodeIdProvider;
import edu.stanford.owl2lpg.translator.visitors.VisitorFactory;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A collection of factory methods to create OWL object translators.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class TranslatorFactory {

  @Nonnull
  public static OntologyTranslator getOntologyTranslator(@Nonnull NodeIdProvider nodeIdProvider) {
    var nodeIdMapper = getNodeIdMapper(nodeIdProvider);
    return new OntologyTranslator(new VisitorFactory(nodeIdMapper).createOntologyVisitor());
  }

  @Nonnull
  public static OntologyTranslator getOntologyTranslator() {
    return getOntologyTranslator(getDefaultProvider());
  }

  @Nonnull
  public static AxiomTranslator getAxiomTranslator(@Nonnull NodeIdProvider nodeIdProvider) {
    var nodeIdMapper = getNodeIdMapper(nodeIdProvider);
    return new AxiomTranslator(new VisitorFactory(nodeIdMapper).createAxiomVisitor());
  }

  @Nonnull
  public static AxiomTranslator getAxiomTranslator() {
    return getAxiomTranslator(getDefaultProvider());
  }

  @Nonnull
  public static ClassExpressionTranslator getClassExpressionTranslator(@Nonnull NodeIdProvider nodeIdProvider) {
    var nodeIdMapper = getNodeIdMapper(nodeIdProvider);
    return new ClassExpressionTranslator(new VisitorFactory(nodeIdMapper).createClassExpressionVisitor());
  }

  @Nonnull
  public static ClassExpressionTranslator getClassExpresssionTranslator() {
    return getClassExpressionTranslator(getDefaultProvider());
  }

  @Nonnull
  public static PropertyExpressionTranslator getPropertyExpressionTranslator(@Nonnull NodeIdProvider nodeIdProvider) {
    var nodeIdMapper = getNodeIdMapper(nodeIdProvider);
    return new PropertyExpressionTranslator(new VisitorFactory(nodeIdMapper).createPropertyExpressionVisitor());
  }

  @Nonnull
  public static PropertyExpressionTranslator getPropertyExpressionTranslator() {
    return getPropertyExpressionTranslator(getDefaultProvider());
  }

  @Nonnull
  public static EntityTranslator getEntityTranslator(@Nonnull NodeIdProvider nodeIdProvider) {
    var nodeIdMapper = getNodeIdMapper(nodeIdProvider);
    return new EntityTranslator(new VisitorFactory(nodeIdMapper).createEntityVisitor());
  }

  @Nonnull
  public static EntityTranslator getEntityTranslator() {
    return getEntityTranslator(getDefaultProvider());
  }

  @Nonnull
  public static IndividualTranslator getIndividualTranslator(@Nonnull NodeIdProvider nodeIdProvider) {
    var nodeIdMapper = getNodeIdMapper(nodeIdProvider);
    return new IndividualTranslator(new VisitorFactory(nodeIdMapper).createIndividualVisitor());
  }

  @Nonnull
  public static IndividualTranslator getIndividualTranslator() {
    return getIndividualTranslator(getDefaultProvider());
  }

  @Nonnull
  public static DataRangeTranslator getDataRangeTranslator(@Nonnull NodeIdProvider nodeIdProvider) {
    var nodeIdMapper = getNodeIdMapper(nodeIdProvider);
    return new DataRangeTranslator(new VisitorFactory(nodeIdMapper).createDataVisitor());
  }

  @Nonnull
  public static DataRangeTranslator getDataRangeTranslator() {
    return getDataRangeTranslator(getDefaultProvider());
  }

  @Nonnull
  public static LiteralTranslator getLiteralTranslator(@Nonnull NodeIdProvider nodeIdProvider) {
    var nodeIdMapper = getNodeIdMapper(nodeIdProvider);
    return new LiteralTranslator(new VisitorFactory(nodeIdMapper).createDataVisitor());
  }

  @Nonnull
  public static LiteralTranslator getLiteralTranslator() {
    return getLiteralTranslator(getDefaultProvider());
  }

  private static NodeIdMapper getNodeIdMapper(@Nonnull NodeIdProvider nodeIdProvider) {
    checkNotNull(nodeIdProvider);
    return new NodeIdMapper(nodeIdProvider);
  }

  private static NodeIdProvider getDefaultProvider() {
    return new NumberIncrementIdProvider();
  }
}
