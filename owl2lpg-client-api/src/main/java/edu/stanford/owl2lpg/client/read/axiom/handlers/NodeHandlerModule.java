package edu.stanford.owl2lpg.client.read.axiom.handlers;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = OwlDataFactoryModule.class)
public abstract class NodeHandlerModule {

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlAnnotationNodeHandler(OwlAnnotationNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlEntityDeclarationNodeHandler(OwlEntityDeclarationNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlSubClassOfNodeHandler(OwlSubClassOfNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlAnnotationAssertionNodeHandler(OwlAnnotationAssertionNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlObjectSomeValuesFromNodeHandler(OwlObjectSomeValuesFromNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlObjectAllValuesFromNodeHandler(OwlObjectAllValuesFromNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlDataSomeValuesFromNodeHandler(OwlDataSomeValuesFromNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlObjectMaxCardinalityNodeHandler(OwlObjectMaxCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlObjectMinCardinalityNodeHandler(OwlObjectMinCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlObjectExactCardinalityNodeHandler(OwlObjectExactCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlDataMaxCardinalityNodeHandler(OwlDataMaxCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlDataMinCardinalityNodeHandler(OwlDataMinCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlDataExactCardinalityNodeHandler(OwlDataExactCardinalityNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlDataAllValuesFromNodeHandler(OwlDataAllValuesFromNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlClassNodeHandler(OwlClassNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlObjectPropertyNodeHandler(OwlObjectPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlDataPropertyNodeHandler(OwlDataPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlAnnotationPropertyNodeHandler(OwlAnnotationPropertyNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlNamedIndividualNodeHandler(OwlNamedIndividualNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlDatatypeNodeHandler(OwlDatatypeNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlLiteralNodeHandler(OwlLiteralNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideIriNodeHandler(IriNodeHandler handler);
}
