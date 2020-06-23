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
  public abstract NodeHandler<?> provideEntityDeclarationHandler(EntityDeclarationHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideSubClassOfHandler(SubClassOfHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlClassHandler(ClassHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlObjectPropertyHandler(ObjectPropertyHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDataPropertyHandler(DataPropertyHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlAnnotationPropertyHandler(AnnotationPropertyHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlNamedIndividualHandler(NamedIndividualHandler handler);

  @Binds
  @IntoSet
  public abstract NodeHandler<?> provideOwlDatatypeHandler(DatatypeHandler handler);
}
