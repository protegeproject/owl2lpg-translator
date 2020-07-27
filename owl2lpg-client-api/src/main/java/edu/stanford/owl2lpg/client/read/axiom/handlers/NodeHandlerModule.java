package edu.stanford.owl2lpg.client.read.axiom.handlers;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class NodeHandlerModule {

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideIriNodeHandler(IriNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlAnnotationAssertionNodeHandler(OwlAnnotationAssertionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlAnnotationNodeHandler(OwlAnnotationNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlAnnotationPropertyDomainNodeHandler(OwlAnnotationPropertyDomainNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlAnnotationPropertyNodeHandler(OwlAnnotationPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlAnnotationPropertyRangeNodeHandler(OwlAnnotationPropertyRangeNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlAsymmetricObjectPropertyNodeHandler(OwlAsymmetricObjectPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlClassAssertionNodeHandler(OwlClassAssertionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlClassNodeHandler(OwlClassNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataAllValuesFromNodeHandler(OwlDataAllValuesFromNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataComplementOfNodeHandler(OwlDataComplementOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataExactCardinalityNodeHandler(OwlDataExactCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataHasValueNodeHandler(OwlDataHasValueNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataIntersectionOfNodeHandler(OwlDataIntersectionOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataMaxCardinalityNodeHandler(OwlDataMaxCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataMinCardinalityNodeHandler(OwlDataMinCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataOneOfNodeHandler(OwlDataOneOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataPropertyAssertionNodeHandler(OwlDataPropertyAssertionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataPropertyDomainNodeHandler(OwlDataPropertyDomainNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataPropertyNodeHandler(OwlDataPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataPropertyRangeNodeHandler(OwlDataPropertyRangeNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataSomeValuesFromNodeHandler(OwlDataSomeValuesFromNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDatatypeDefinitionNodeHandler(OwlDatatypeDefinitionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDatatypeNodeHandler(OwlDatatypeNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDatatypeRestrictionNodeHandler(OwlDatatypeRestrictionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataUnionOfNodeHandler(OwlDataUnionOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDifferentIndividualsNodeHandler(OwlDifferentIndividualsNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDisjointClassesNodeHandler(OwlDisjointClassesNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDisjointDataPropertiesNodeHandler(OwlDisjointDataPropertiesNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDisjointObjectPropertiesNodeHandler(OwlDisjointObjectPropertiesNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDisjointUnionNodeHandler(OwlDisjointUnionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlEntityDeclarationNodeHandler(OwlEntityDeclarationNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlEquivalentClassesNodeHandler(OwlEquivalentClassesNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlEquivalentDataPropertiesNodeHandler(OwlEquivalentDataPropertiesNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlEquivalentObjectPropertiesNodeHandler(OwlEquivalentObjectPropertiesNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlFacetNodeHandler(OwlFacetNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlFacetRestrictionNodeHandler(OwlFacetRestrictionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlFunctionalDataPropertyNodeHandler(OwlFunctionalDataPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlFunctionalObjectPropertyNodeHandler(OwlFunctionalObjectPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlHasKeyNodeHandler(OwlHasKeyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlInverseFunctionalObjectPropertyNodeHandler(OwlInverseFunctionalObjectPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlInverseObjectPropertiesNodeHandler(OwlInverseObjectPropertiesNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlIrreflexiveObjectPropertyNodeHandler(OwlIrreflexiveObjectPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlLiteralNodeHandler(OwlLiteralNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlNamedIndividualNodeHandler(OwlNamedIndividualNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlNegativeDataPropertyAssertionNodeHandler(OwlNegativeDataPropertyAssertionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlNegativeObjectPropertyAssertionNodeHandler(OwlNegativeObjectPropertyAssertionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectAllValuesFromNodeHandler(OwlObjectAllValuesFromNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectComplementOfNodeHandler(OwlObjectComplementOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectExactCardinalityNodeHandler(OwlObjectExactCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectHasSelfNodeHandler(OwlObjectHasSelfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectHasValueNodeHandler(OwlObjectHasValueNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectIntersectionOfNodeHandler(OwlObjectIntersectionOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectInverseOfNodeHandler(OwlObjectInverseOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectMaxCardinalityNodeHandler(OwlObjectMaxCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectMinCardinalityNodeHandler(OwlObjectMinCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectOneOfNodeHandler(OwlObjectOneOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectPropertyAssertionNodeHandler(OwlObjectPropertyAssertionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectPropertyDomainNodeHandler(OwlObjectPropertyDomainNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectPropertyNodeHandler(OwlObjectPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectPropertyRangeNodeHandler(OwlObjectPropertyRangeNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectSomeValuesFromNodeHandler(OwlObjectSomeValuesFromNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectUnionOfNodeHandler(OwlObjectUnionOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlReflexiveObjectPropertyNodeHandler(OwlReflexiveObjectPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlSameIndividualNodeHandler(OwlSameIndividualNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlSubAnnotationPropertyOfNodeHandler(OwlSubAnnotationPropertyOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlSubClassOfNodeHandler(OwlSubClassOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlSubDataPropertyOfNodeHandler(OwlSubDataPropertyOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlSubObjectPropertyOfNodeHandler(OwlSubObjectPropertyOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlSymmetricObjectPropertyNodeHandler(OwlSymmetricObjectPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlTransitiveObjectPropertyNodeHandler(OwlTransitiveObjectPropertyNodeHandler handler);
}
