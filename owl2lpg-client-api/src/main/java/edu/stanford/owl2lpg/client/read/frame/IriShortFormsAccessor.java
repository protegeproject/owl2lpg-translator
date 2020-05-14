package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import edu.stanford.bmir.protege.web.shared.shortform.ShortForm;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface IriShortFormsAccessor {

  Multimap<IRI, ShortForm> getDictionaryMap(@Nonnull ImmutableList<IRI> entityIris,
                                            @Nonnull ImmutableList<IRI> annotationPropertyIris);
}
