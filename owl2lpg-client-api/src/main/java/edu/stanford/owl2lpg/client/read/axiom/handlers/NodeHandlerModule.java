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
  provideOwlEntityDeclarationNodeHandler(OwlEntityDeclarationNodeHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?>
  provideOwlSubClassOfNodeHandler(OwlSubClassOfNodeHandler handler);

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
}
