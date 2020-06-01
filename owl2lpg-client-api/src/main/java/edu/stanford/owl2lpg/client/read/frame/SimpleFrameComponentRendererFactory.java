package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.Multimap;
import edu.stanford.bmir.protege.web.shared.shortform.ShortForm;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SimpleFrameComponentRendererFactory {

  @Inject
  public SimpleFrameComponentRendererFactory() {
  }

  public static SimpleFrameComponentRenderer create(@Nonnull Multimap<IRI, ShortForm> dictionaryMap) {
    return new SimpleFrameComponentRenderer(dictionaryMap);
  }

  public SimpleFrameComponentRenderer get(@Nonnull Multimap<IRI, ShortForm> dictionaryMap) {
    checkNotNull(dictionaryMap);
    return create(dictionaryMap);
  }
}
