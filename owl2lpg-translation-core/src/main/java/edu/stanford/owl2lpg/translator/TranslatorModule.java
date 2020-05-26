package edu.stanford.owl2lpg.translator;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.translator.visitors.AxiomVisitor;
import edu.stanford.owl2lpg.translator.visitors.NodeIdMapper;
import edu.stanford.owl2lpg.translator.visitors.NodeIdProvider;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class TranslatorModule {

  @Provides
  @TranslationSessionScope
  NodeIdProvider provideNodeIdProvider() {
    return new NumberIncrementIdProvider();
  }
}
