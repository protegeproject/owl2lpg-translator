package edu.stanford.owl2lpg.client.read.handlers;

import dagger.Module;
import dagger.Provides;
import org.semanticweb.owlapi.model.OWLDataFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class OwlDataFactoryModule {

  @Provides
  public OWLDataFactory provideOwlDataFactory() {
    return new OWLDataFactoryImpl();
  }
}
